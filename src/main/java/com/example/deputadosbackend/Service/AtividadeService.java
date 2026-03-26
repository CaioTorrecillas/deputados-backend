package com.example.deputadosbackend.Service;

import com.example.deputadosbackend.Dto.AtividadeDTO;
import com.example.deputadosbackend.Model.Atividade;
import com.example.deputadosbackend.Repository.AtividadeRepository;
import com.example.deputadosbackend.Repository.DeputadoRepository;
import com.example.deputadosbackend.Repository.ProposicaoRepository;
import com.example.deputadosbackend.WebClient.AtividadeClient;
import com.example.deputadosbackend.WebClient.ProposicaoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AtividadeService {

    private final AtividadeClient atividadeClient;

    private static final Logger log = LoggerFactory.getLogger(AtividadeService.class);
    private AtividadeRepository atividadeRepository;

    public AtividadeService(AtividadeClient atividadeClient, AtividadeRepository atividadeRepository) {
        this.atividadeClient = atividadeClient;
        this.atividadeRepository = atividadeRepository;

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

}
