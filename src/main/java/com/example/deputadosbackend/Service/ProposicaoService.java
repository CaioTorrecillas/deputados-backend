package com.example.deputadosbackend.Service;

import com.example.deputadosbackend.Dto.*;
import com.example.deputadosbackend.Model.Deputado;
import com.example.deputadosbackend.Model.Proposicao;
import com.example.deputadosbackend.Repository.DeputadoRepository;
import com.example.deputadosbackend.Repository.ProposicaoRepository;
import com.example.deputadosbackend.Response.ProposicaoDetalheResponse;

import com.example.deputadosbackend.WebClient.ProposicaoClient;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProposicaoService {

    private final ProposicaoClient proposicaoClient;
    private final ProposicaoRepository proposicaoRepository;
    private static final Logger log = LoggerFactory.getLogger(ProposicaoService.class);
    @Value("${google.api.key}")
    private String apiKey;
    private final DeputadoRepository  deputadoRepository;
    public ProposicaoService(ProposicaoClient proposicaoClient, DeputadoRepository deputadoRepository,ProposicaoRepository proposicaoRepository) {
        this.proposicaoClient = proposicaoClient;
        this.proposicaoRepository = proposicaoRepository;
        this.deputadoRepository = deputadoRepository;
    }



    public PageResponseDTO<ProposicaoDTO> listarProposicoesDeputado(
            Long idDeputado,
            int pagina,
            String tipo
    ) {

        Pageable pageable = PageRequest.of(pagina - 1, 30);

        Page<Proposicao> page;

        if (tipo != null && !tipo.isEmpty()) {
            page = proposicaoRepository
                    .findByDeputados_IdAndSiglaTipo(idDeputado, tipo, pageable);
        } else {
            page = proposicaoRepository
                    .findByDeputados_Id(idDeputado, pageable);
        }

        List<ProposicaoDTO> dtos = page.getContent().stream().map(p -> {
            ProposicaoDTO dto = new ProposicaoDTO();

            dto.setId(p.getId());
            dto.setSiglaTipo(p.getSiglaTipo());
            dto.setNumero(p.getNumero());
            dto.setAno(p.getAno());
            dto.setEmenta(p.getEmenta());
            dto.setDataApresentacao(p.getDataApresentacao());

            return dto;
        }).toList();

        return new PageResponseDTO<>(
                dtos,
                pagina,
                page.getSize(),
                (int) page.getTotalElements()
        );
    }

    public SyncResponseDTO sincronizarProjetosDeLei() {

        List<ProposicaoDTO> proposicoes = proposicaoClient.buscarProjetosDeLei2025();

        SyncResponseDTO resultado = new SyncResponseDTO();

        for (ProposicaoDTO dto : proposicoes) {

            Proposicao existente = proposicaoRepository
                    .findById(dto.getId())
                    .orElse(null);

            if (existente != null) {

                boolean precisaAtualizar = false;

                if (!Objects.equals(existente.getEmenta(), dto.getEmenta())) {
                    existente.setEmenta(dto.getEmenta());
                    precisaAtualizar = true;
                }

                if (!Objects.equals(existente.getNumero(), dto.getNumero())) {
                    existente.setNumero(dto.getNumero());
                    precisaAtualizar = true;
                }

                if (!Objects.equals(existente.getAno(), dto.getAno())) {
                    existente.setAno(dto.getAno());
                    precisaAtualizar = true;
                }

                if (!Objects.equals(existente.getSiglaTipo(), dto.getSiglaTipo())) {
                    existente.setSiglaTipo(dto.getSiglaTipo());
                    precisaAtualizar = true;
                }

                if (!Objects.equals(existente.getDataApresentacao(), dto.getDataApresentacao())) {
                    existente.setDataApresentacao(dto.getDataApresentacao());
                    precisaAtualizar = true;
                }

                if (precisaAtualizar) {
                    proposicaoRepository.save(existente);
                    resultado.setAtualizados(resultado.getAtualizados() + 1);
                    resultado.getIdsAtualizados().add(dto.getId());
                } else {
                    resultado.setNaoAlterados(resultado.getNaoAlterados() + 1);
                }

            } else {

                Proposicao nova = new Proposicao();

                nova.setId(dto.getId());
                nova.setSiglaTipo(dto.getSiglaTipo());
                nova.setNumero(dto.getNumero());
                nova.setAno(dto.getAno());
                nova.setEmenta(dto.getEmenta());
                nova.setDataApresentacao(dto.getDataApresentacao());

                proposicaoRepository.save(nova);
                resultado.setNovos(resultado.getNovos() + 1);
            }
        }

        return resultado;
    }

    public void vincularAutoresProposicoes() {

        List<Proposicao> proposicoes = proposicaoRepository.findAll();
        int processadas = 0;

        for (Proposicao proposicao : proposicoes) {

            try {
                System.out.println("Processando PL: " + proposicao.getId());

                List<AutorDTO> autores = proposicaoClient.buscarAutores(proposicao.getId());
                System.out.println("Autores da PL " + proposicao.getId() + ":");

                autores.forEach(a -> System.out.println(
                        " - ID: " + a.getId() +
                                " | Nome: " + a.getNome() +
                                " | Tipo: " + a.getTipo()
                ));

                Set<Deputado> deputadosSet = new HashSet<>();

                for (AutorDTO autor : autores) {

                    if (autor.getTipo() == null || !autor.getTipo().toLowerCase().contains("deputado")) {
                        continue;
                    }

                    Deputado deputado = null;

                    // Tenta pelo ID
                    if (autor.getId() != null) {
                        deputado = deputadoRepository.findById(autor.getId().longValue()).orElse(null);
                    }

                    // Fallback pelo NOME
                    if (deputado == null && autor.getNome() != null) {
                        deputado = deputadoRepository.findByNome(autor.getNome()).orElse(null);
                    }

                    if (deputado != null) {
                        deputadosSet.add(deputado);
                    } else {
                        System.out.println("Deputado não encontrado: " + autor.getNome());
                    }
                }

                List<Deputado> deputados = new ArrayList<>(deputadosSet);


                Set<Long> atuais = proposicao.getDeputados() == null
                        ? new HashSet<>()
                        : proposicao.getDeputados().stream()
                        .map(Deputado::getId)
                        .collect(Collectors.toSet());

                Set<Long> novos = deputados.stream()
                        .map(Deputado::getId)
                        .collect(Collectors.toSet());

                boolean mudou = !atuais.equals(novos);

                if (!deputados.isEmpty() && mudou) {
                    proposicao.setDeputados(deputados);
                    proposicaoRepository.save(proposicao);
                    processadas++;
                    System.out.println("PL atualizada: " + proposicao.getId() + " | Deputados: " + deputados.size());
                } else if (!mudou) {
                    System.out.println("PL não alterada: " + proposicao.getId() + " (mesmos deputados)");
                } else {
                    System.out.println("Nenhum deputado válido encontrado para PL: " + proposicao.getId());
                }

            } catch (Exception e) {
                System.out.println("Erro na PL: " + proposicao.getId());
                e.printStackTrace();
            }
        }

        System.out.println("Total de PLs processadas e que tiveram mudanças: " + processadas);
    }
    public ProposicaoPLDetalheDTO buscarProposicaoDetalhe(Long id){
        ProposicaoDetalheResponse response = new ProposicaoDetalheResponse();

        ProposicaoPLDetalheDTO detalheProposicao = proposicaoClient.buscarDetalheProposicaoPL(id);
        response.setDados(detalheProposicao);
        return detalheProposicao;
    }

    public String gerarResumoProposicao(Long idProposicao) {
        Client clientAi =  Client.builder().apiKey(apiKey).build();


        Proposicao proposicao = proposicaoRepository.findById(idProposicao)
                .orElseThrow(() -> new RuntimeException("Proposição não encontrada"));

        // 1. Validação
        if (proposicao.getEmenta() == null || proposicao.getEmenta().isBlank()) {
            return "Sem ementa disponível.";
        }

        // 2. Cache (evita custo desnecessário)
        if (proposicao.getResumoIa() != null && !proposicao.getResumoIa().isBlank()) {
            return proposicao.getResumoIa();
        }

        try {
            // 3. Prompt melhor estruturado
            String prompt = """
            Resuma a seguinte ementa de projeto de lei de forma simples e objetiva.
            Use linguagem acessível para qualquer cidadão.
            Máximo de 3 frases.

            Ementa:
            %s
            """.formatted(proposicao.getEmenta());

            GenerateContentResponse response =
                    clientAi.models.generateContent(
                            "gemini-2.5-flash-lite",
                            prompt,
                            null
                    );


                String resumo = response.text();

            if (resumo == null || resumo.isBlank()) {
                throw new RuntimeException("Resposta da IA vazia");
            }


            proposicao.setResumoIa(resumo);
            proposicaoRepository.save(proposicao);

            return resumo;

        } catch (Exception e) {
            // 6. Fallback seguro
            System.out.println("Não foi possível gerar o resumo automaticamente no momento.Exception: " + e);
            return "Não foi possível gerar o resumo automaticamente no momento.Exception: " + e;
        }
    }




    public ProposicoesDadosTotaisDTO  listarProposicoesDeputadoDadosTotais(Long idDeputado){
        List<ProposicaoDTO> proposicoes =
                proposicaoClient.buscarProposicoesDeputadoDadosTotais(idDeputado);

        Map<String, Long> porTipo =
                proposicoes.stream()
                        .collect(Collectors.groupingBy(
                                ProposicaoDTO::getSiglaTipo,
                                Collectors.counting()
                        ));
        int total = proposicoes.size();

        return new ProposicoesDadosTotaisDTO(total, porTipo);
    }
}
