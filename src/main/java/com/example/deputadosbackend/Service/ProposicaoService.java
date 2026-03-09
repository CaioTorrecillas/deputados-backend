package com.example.deputadosbackend.Service;

import com.example.deputadosbackend.Dto.PageResponseDTO;
import com.example.deputadosbackend.Dto.ProposicaoDTO;
import com.example.deputadosbackend.Dto.ProposicaoPLDetalheDTO;
import com.example.deputadosbackend.Dto.ProposicoesDadosTotaisDTO;
import com.example.deputadosbackend.Response.DeputadosResponse;
import com.example.deputadosbackend.Response.ProposicaoDetalheResponse;
import com.example.deputadosbackend.Response.ProposicaoResponse;
import com.example.deputadosbackend.WebClient.DeputadosClient;
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

    public ProposicaoService(ProposicaoClient proposicaoClient) {
        this.proposicaoClient = proposicaoClient;
    }


    public ProposicaoResponse listarProposicoes(Integer ano) {
        ProposicaoResponse response = new ProposicaoResponse();

        // regra futura: se ano for null, usar ano atual
        if (ano == null) {
            ano = Year.now().getValue();
        }

        List<ProposicaoDTO> dados = proposicaoClient.buscarProjetosDeLei(ano);


        response.setDados(dados);

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

    public ProposicaoPLDetalheDTO buscarProposicaoDetalhe(Long id){
        ProposicaoDetalheResponse response = new ProposicaoDetalheResponse();

        ProposicaoPLDetalheDTO detalheProposicao = proposicaoClient.buscarDetalheProposicaoPL(id);
        response.setDados(detalheProposicao);
        return detalheProposicao;
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
