package com.example.deputadosbackend.Service;

import com.example.deputadosbackend.Dto.AtividadeDTO;
import com.example.deputadosbackend.Model.Atividade;
import com.example.deputadosbackend.Model.Votacao;
import com.example.deputadosbackend.Model.Voto;
import com.example.deputadosbackend.Repository.*;
import com.example.deputadosbackend.WebClient.AtividadeClient;
import com.example.deputadosbackend.WebClient.ProposicaoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AtividadeService {

    private final AtividadeClient atividadeClient;

    private static final Logger log = LoggerFactory.getLogger(AtividadeService.class);
    private AtividadeRepository atividadeRepository;
    private VotacaoRepository votacaoRepository;
    private VotoRepository votoRepository;

    public AtividadeService(AtividadeClient atividadeClient, AtividadeRepository atividadeRepository, VotacaoRepository votacaoRepository, VotoRepository votoRepository) {
        this.atividadeClient = atividadeClient;
        this.atividadeRepository = atividadeRepository;
        this.votacaoRepository = votacaoRepository;
        this.votoRepository = votoRepository;

    }



    public Map<String, Object> salvarAtividadesVotacoes() {
        List<Atividade> atividadesParaSalvar = new ArrayList<>();
        int totalVotacoes = 0;
        int totalVotos = 0;

        List<Map<String, Object>> votacoes = atividadeClient.buscarVotacoesPlenario25();

        for (Map<String, Object> votacao : votacoes) {
            String votacaoId = (String) votacao.get("id");
            if (votacaoId == null) continue;

            List<Map<String, Object>> votos = atividadeClient.buscarVotosPorVotacao(votacaoId);
            if (votos == null || votos.isEmpty()) continue;

            totalVotacoes++;
            totalVotos += votos.size();

            for (Map<String, Object> voto : votos) {
                Map<String, Object> deputado = (Map<String, Object>) voto.get("deputado_");

                Atividade atividade = new Atividade();
                atividade.setDeputadoId(((Number) deputado.get("id")).longValue());
                atividade.setTipo("VOTACAO");
                atividade.setReferenciaId(votacaoId);

                String dataVotacaoStr = (String) voto.get("dataRegistroVoto");
                if (dataVotacaoStr != null) {
                    atividade.setDataAtividade(LocalDateTime.parse(dataVotacaoStr));
                }

                String tipoVoto = (String) voto.get("tipoVoto");
                String proposicao = voto.get("proposicao") != null ? voto.get("proposicao").toString() : null;

                String descricao = "Votou '" + tipoVoto + "'";
                descricao += proposicao != null ? " na " + proposicao : " no plenário";
                atividade.setDescricao(descricao);

                atividadesParaSalvar.add(atividade);
            }
        }

        atividadeRepository.saveAll(atividadesParaSalvar);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("totalVotacoesProcessadas", totalVotacoes);
        resultado.put("totalVotosSalvos", totalVotos);

        System.out.println("{Atividade Service} | Todas as votações no plenário processadas e salvas");

        return resultado;
    }
    public Map<String, Object> salvarVotacoes() {

        List<Map<String, Object>> votacoesApi = atividadeClient.buscarVotacoesPlenario25();

        List<Votacao> votacoesParaSalvar = new ArrayList<>();

        int criadas = 0;
        int atualizadas = 0;

        for (Map<String, Object> v : votacoesApi) {

            String id = (String) v.get("id");

            if (id == null) {
                continue;
            }

            // 🔍 tenta buscar no banco
            Optional<Votacao> votacaoOpt = votacaoRepository.findById(id);

            Votacao votacao;

            if (votacaoOpt.isPresent()) {
                votacao = votacaoOpt.get();
                atualizadas++;
            } else {
                votacao = new Votacao();
                votacao.setId(id);
                criadas++;
            }

            // 📅 data
            String dataStr = (String) v.get("data");
            if (dataStr != null) {
                votacao.setData(LocalDate.parse(dataStr));
            }

            // 🕒 dataHoraRegistro
            String dataHoraStr = (String) v.get("dataHoraRegistro");
            if (dataHoraStr != null) {
                votacao.setDataHoraRegistro(LocalDateTime.parse(dataHoraStr));
            }

            votacao.setSiglaOrgao((String) v.get("siglaOrgao"));
            votacao.setDescricao((String) v.get("descricao"));
            votacao.setUri((String) v.get("uri"));

            // 🧠 proposição (mesmo se null)
            votacao.setProposicaoObjeto((String) v.get("proposicaoObjeto"));

            // 🔗 uri evento
            votacao.setUriEvento((String) v.get("uriEvento"));

            // ✔ aprovação
            Object aprovacao = v.get("aprovacao");
            if (aprovacao != null) {
                votacao.setAprovacao(((Number) aprovacao).intValue() == 1);
            }

            // 🔥 (opcional) extrair votos da descrição
            extrairResultados(votacao);

            votacoesParaSalvar.add(votacao);
        }

        votacaoRepository.saveAll(votacoesParaSalvar);

        Map<String, Object> result = new HashMap<>();
        result.put("votacoesCriadas", criadas);
        result.put("votacoesAtualizadas", atualizadas);
        result.put("totalProcessadas", votacoesApi.size());

        System.out.println("{Votacao Service} | Finalizado: " + result);

        return result;
    }



    public Map<String, Object> salvarVotos() {

        List<Votacao> votacoes = votacaoRepository.findAll();

        List<Voto> votosParaSalvar = new ArrayList<>();

        int totalVotacoesProcessadas = 0;
        int votosSalvos = 0;
        int votosIgnorados = 0;

        for (Votacao votacao : votacoes) {

            String votacaoId = votacao.getId();

            System.out.println("--------------------------------------------------");
            System.out.println("{Voto Service} | Buscando votos da votação: " + votacaoId);

            List<Map<String, Object>> votosApi =
                    atividadeClient.buscarVotosPorVotacao(votacaoId);

            if (votosApi == null || votosApi.isEmpty()) {
                System.out.println("{Voto Service} | Nenhum voto encontrado");
                continue;
            }

            totalVotacoesProcessadas++;

            for (Map<String, Object> v : votosApi) {

                try {

                    Map<String, Object> deputado =
                            (Map<String, Object>) v.get("deputado_");

                    if (deputado == null) {
                        votosIgnorados++;
                        continue;
                    }

                    Long deputadoId = ((Number) deputado.get("id")).longValue();


                    if (votoRepository.existsByVotacaoIdAndDeputadoId(votacaoId, deputadoId)) {
                        votosIgnorados++;
                        continue;
                    }

                    Voto voto = new Voto();

                    voto.setVotacaoId(votacaoId);
                    voto.setDeputadoId(deputadoId);


                    String tipoVotoStr = (String) v.get("tipoVoto");
                    voto.setTipoVoto(tipoVotoStr); // ou enum depois


                    String dataStr = (String) v.get("dataRegistroVoto");

                    if (dataStr != null) {
                        voto.setDataRegistro(LocalDateTime.parse(dataStr));
                    } else {
                        // fallback seguro
                        voto.setDataRegistro(LocalDateTime.now());
                    }

                    votosParaSalvar.add(voto);
                    votosSalvos++;

                } catch (Exception e) {
                    votosIgnorados++;
                    System.out.println("{Voto Service} | Erro ao processar voto: " + e.getMessage());
                }
            }
        }

        // 💾 salva tudo de uma vez (performance)
        votoRepository.saveAll(votosParaSalvar);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("votacoesProcessadas", totalVotacoesProcessadas);
        resultado.put("votosSalvos", votosSalvos);
        resultado.put("votosIgnorados", votosIgnorados);

        System.out.println("==================================================");
        System.out.println("{Voto Service} | Finalizado: " + resultado);

        return resultado;
    }

    private void extrairResultados(Votacao votacao) {

        String descricao = votacao.getDescricao();

        if (descricao == null) return;

        try {
            Pattern simPattern = Pattern.compile("Sim:\\s*(\\d+)");
            Pattern naoPattern = Pattern.compile("Não:\\s*(\\d+)");
            Pattern totalPattern = Pattern.compile("Total:\\s*(\\d+)");

            Matcher simMatcher = simPattern.matcher(descricao);
            Matcher naoMatcher = naoPattern.matcher(descricao);
            Matcher totalMatcher = totalPattern.matcher(descricao);

            if (simMatcher.find()) {
                votacao.setVotosSim(Integer.parseInt(simMatcher.group(1)));
            }

            if (naoMatcher.find()) {
                votacao.setVotosNao(Integer.parseInt(naoMatcher.group(1)));
            }

            if (totalMatcher.find()) {
                votacao.setTotalVotos(Integer.parseInt(totalMatcher.group(1)));
            }

        } catch (Exception e) {
            System.out.println("{Votacao Service} | Erro ao extrair resultados: " + e.getMessage());
        }
    }
    public List<Atividade> buscarAtividadesPorDeputado(Long deputadoId) {

        List<Atividade> atividades =
                atividadeRepository.findByDeputadoIdOrderByDataAtividadeDesc(deputadoId);

        if (atividades.isEmpty()) {
            System.out.println("{Atividade Service} | Nenhuma atividade encontrada para deputado: " + deputadoId);
        }

        return atividades;
    }
    public Map<String, Object> gerarAtividades() {

        List<Voto> votos = votoRepository.findAll();
        List<Atividade> atividades = new ArrayList<>();

        int criadas = 0;
        int ignoradas = 0;

        for (Voto voto : votos) {

            // 🚫 evita duplicar atividade
            boolean jaExiste = atividadeRepository
                    .existsByReferenciaIdAndDeputadoId(
                            voto.getVotacaoId(),
                            voto.getDeputadoId()
                    );

            if (jaExiste) {
                ignoradas++;
                continue;
            }

            Votacao votacao = votacaoRepository
                    .findById(voto.getVotacaoId())
                    .orElse(null);

            if (votacao == null) {
                ignoradas++;
                continue;
            }

            Atividade atividade = new Atividade();

            atividade.setDeputadoId(voto.getDeputadoId());
            atividade.setTipo("VOTACAO");
            atividade.setReferenciaId(voto.getVotacaoId());
            atividade.setDataAtividade(voto.getDataRegistro());

            // 🔥 descrição inteligente
            String descricao = "Votou '" + voto.getTipoVoto() + "'";

            if (votacao.getDescricao() != null) {
                descricao += " — " + votacao.getDescricao();
            }

            atividade.setDescricao(descricao);

            atividades.add(atividade);
            criadas++;
        }

        atividadeRepository.saveAll(atividades);

        Map<String, Object> result = new HashMap<>();
        result.put("atividadesCriadas", criadas);
        result.put("atividadesIgnoradas", ignoradas);

        return result;
    }
}
