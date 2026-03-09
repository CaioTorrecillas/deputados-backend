package com.example.deputadosbackend.WebClient;

import com.example.deputadosbackend.Dto.PageResponseDTO;
import com.example.deputadosbackend.Dto.ProposicaoDTO;
import com.example.deputadosbackend.Dto.ProposicaoPLDetalheDTO;
import com.example.deputadosbackend.Response.DeputadosResponse;
import com.example.deputadosbackend.Response.ProposicaoDadosTotaisResponse;
import com.example.deputadosbackend.Response.ProposicaoResponse;
import com.example.deputadosbackend.Response.ProposicaoDetalheResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProposicaoClient {

    private final WebClient webClient;
    ProposicaoClient() {
        this.webClient = WebClient.builder()
            .baseUrl("https://dadosabertos.camara.leg.br/api/v2")
            .build();
    }

    public List<ProposicaoDTO> buscarProjetosDeLei(Integer ano) {
        UriComponentsBuilder uri = UriComponentsBuilder
                .fromPath("/proposicoes")
                .queryParam("siglaTipo", "PL");

        if (ano != null) {
            uri.queryParam("ano", ano);
        }
        return webClient
                .get()
                .uri(uri.build().toUriString())
                .retrieve()
                .bodyToMono(ProposicaoResponse.class)
                .map(ProposicaoResponse::getDados)
                .block();


    }
    public ProposicaoPLDetalheDTO buscarDetalheProposicaoPL(Long id) {
        return webClient
                .get()
                .uri("/proposicoes/{id}", id)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Erro da API: " + body))
                )
                .bodyToMono(ProposicaoDetalheResponse.class)
                .map(ProposicaoDetalheResponse::getDados)
                .block();
    }

    public Mono<ProposicaoResponse> buscarProposicoesPorDeputado(
            Long idDeputado,
            int pagina
    ) {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/proposicoes")
                        .queryParam("idDeputadoAutor", idDeputado)
                        .queryParam("itens", 100)
                        .queryParam("pagina", pagina)
                        .build()
                )
                .retrieve()
                .bodyToMono(ProposicaoResponse.class);
    }

    public List<ProposicaoDTO> buscarProposicoesDeputadoDadosTotais(Long idDeputado) {

        List<ProposicaoDTO> todas = new ArrayList<>();

        int pagina = 1;
        boolean temProxima = true;

        while (temProxima) {
            int paginaAtual = pagina;

            ProposicaoDadosTotaisResponse<ProposicaoDTO> response = webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/proposicoes")
                            .queryParam("idDeputadoAutor", idDeputado)
                            .queryParam("itens", 100)
                                .queryParam("pagina", paginaAtual)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ProposicaoDadosTotaisResponse<ProposicaoDTO>>() {})
                    .block();

            todas.addAll(response.getDados());

            temProxima = response.getLinks()
                    .stream()
                    .anyMatch(link -> "next".equals(link.getRel()));

            pagina++;
        }

        return todas;
    }

}
