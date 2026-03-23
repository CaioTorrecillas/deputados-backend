

package com.example.deputadosbackend.WebClient;

import com.example.deputadosbackend.Dto.*;
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
    public List<AutorDTO> buscarAutores(Long idProposicao) {

        AutoresResponseDTO response = webClient.get()
                .uri("/proposicoes/{id}/autores", idProposicao)
                .retrieve()
                .bodyToMono(AutoresResponseDTO.class)
                .block();

        return response != null ? response.getDados() : new ArrayList<>();
    }


    public List<ProposicaoDTO> buscarProjetosDeLei2025() {

        List<ProposicaoDTO> todasProposicoes = new ArrayList<>();

        int pagina = 1;
        int itens = 100; // máximo permitido normalmente

        while (true) {

            UriComponentsBuilder uri = UriComponentsBuilder
                    .fromPath("/proposicoes")
                    .queryParam("siglaTipo", "PL")
                    .queryParam("ano", 2025)
                    .queryParam("ordenarPor", "id")
                    .queryParam("ordem", "ASC")
                    .queryParam("pagina", pagina)
                    .queryParam("itens", itens);




            ProposicaoResponse response = webClient
                    .get()
                    .uri(uri.build().toUriString())
                    .retrieve()
                    .bodyToMono(ProposicaoResponse.class)
                    .block();

            if (response == null || response.getDados().isEmpty()) {
                break;
            }

            todasProposicoes.addAll(response.getDados());

            // se veio menos que o limite, acabou
            if (response.getDados().size() < itens) {
                break;
            }

            pagina++;
        }

        return todasProposicoes;
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
            int pagina,
            String tipo
    ) {

        return webClient
                .get()
                .uri(uriBuilder -> {
                    uriBuilder
                            .path("/proposicoes")
                            .queryParam("idDeputadoAutor", idDeputado)
                            .queryParam("itens", 100)
                            .queryParam("pagina", pagina);

                    if (tipo != null && !tipo.isEmpty()) {
                        uriBuilder.queryParam("siglaTipo", tipo);
                    }

                    return uriBuilder.build();
                })
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