package com.example.deputadosbackend.Service;

import com.example.deputadosbackend.Controller.ProposicaoController;
import com.example.deputadosbackend.Dto.PageResponseDTO;
import com.example.deputadosbackend.Dto.ProposicaoDTO;
import com.example.deputadosbackend.Dto.ProposicaoPLDetalheDTO;
import com.example.deputadosbackend.Dto.ProposicoesDadosTotaisDTO;
import com.example.deputadosbackend.Model.Proposicao;
import com.example.deputadosbackend.Repository.ProposicaoRepository;
import com.example.deputadosbackend.Response.DeputadosResponse;
import com.example.deputadosbackend.Response.ProposicaoDetalheResponse;
import com.example.deputadosbackend.Response.ProposicaoResponse;
import com.example.deputadosbackend.WebClient.DeputadosClient;
import com.example.deputadosbackend.WebClient.HuggingFaceClient;
import com.example.deputadosbackend.WebClient.ProposicaoClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProposicaoService {

    private final ProposicaoClient proposicaoClient;
    private final ProposicaoRepository proposicaoRepository;
    private final HuggingFaceClient huggingFaceClient;
    public ProposicaoService(ProposicaoClient proposicaoClient, ProposicaoRepository proposicaoRepository, HuggingFaceClient huggingFaceClient) {
        this.proposicaoClient = proposicaoClient;
        this.proposicaoRepository = proposicaoRepository;
        this.huggingFaceClient = huggingFaceClient;
    }


    public ProposicaoResponse listarProposicoes(Integer ano) {

        List<Proposicao> proposicoes =
                proposicaoRepository.findByAnoOrderByDataApresentacaoDesc(ano);

        List<ProposicaoDTO> dtos = proposicoes.stream().map(p -> {
            ProposicaoDTO dto = new ProposicaoDTO();

            dto.setId(p.getId());
            dto.setSiglaTipo(p.getSiglaTipo());
            dto.setNumero(p.getNumero());
            dto.setAno(p.getAno());
            dto.setEmenta(p.getEmenta());
            dto.setDataApresentacao(p.getDataApresentacao());

            return dto;
        }).toList();

        ProposicaoResponse response = new ProposicaoResponse();
        response.setDados(dtos);

        return response;
    }
    public Mono<PageResponseDTO<ProposicaoDTO>> listarProposicoesDeputado(Long idDeputado, int pagina) {

        return proposicaoClient
                .buscarProposicoesPorDeputado(idDeputado, pagina)
                .map(response -> new PageResponseDTO<>(
                        response.getDados(),
                        pagina,
                        30,
                        response.getDados().size()
                ));
    }
    public void sincronizarProjetosDeLei(Integer ano) {

        List<ProposicaoDTO> proposicoes = proposicaoClient.buscarProjetosDeLei(ano);

        for (ProposicaoDTO dto : proposicoes) {

            if (!proposicaoRepository.existsById(dto.getId())) {

                Proposicao proposicao = new Proposicao();

                proposicao.setId(dto.getId());
                proposicao.setSiglaTipo(dto.getSiglaTipo());
                proposicao.setNumero(dto.getNumero());
                proposicao.setAno(dto.getAno());
                proposicao.setEmenta(dto.getEmenta());
                proposicao.setDataApresentacao(dto.getDataApresentacao());

                proposicaoRepository.save(proposicao);
            }
        }
    }
    public ProposicaoPLDetalheDTO buscarProposicaoDetalhe(Long id){
        ProposicaoDetalheResponse response = new ProposicaoDetalheResponse();

        ProposicaoPLDetalheDTO detalheProposicao = proposicaoClient.buscarDetalheProposicaoPL(id);
        response.setDados(detalheProposicao);
        return detalheProposicao;
    }
    public String buscarOuGerarResumo(Long id) {

        Proposicao p = proposicaoRepository.findById(id)
                .orElseThrow();

        if (p.getResumoIa() != null) {
            return p.getResumoIa();
        }


        String resumo = huggingFaceClient.gerarResumo(p.getEmenta());

        p.setResumoIa(resumo);

        proposicaoRepository.save(p);

        return resumo;
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
