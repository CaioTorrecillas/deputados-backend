package com.example.deputadosbackend.WebClient;

import com.example.deputadosbackend.Dto.*;
import com.example.deputadosbackend.Response.DeputadoDespesasResponseDTO;
import org.springframework.stereotype.Component;
import com.example.deputadosbackend.Response.DeputadosResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class DeputadosClient {

    private final WebClient webClient;

    public DeputadosClient() {
        this.webClient = WebClient.builder()
                .baseUrl("https://dadosabertos.camara.leg.br/api/v2")
                .build();
    }

    public List<DeputadosDTO> sincronizarDeputados() {
        List<DeputadosDTO> todos = new ArrayList<>();
        int pagina = 1;
        int itens = 100;

        while (true) {
            int paginaAtual = pagina;

            DeputadosResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/deputados")
                            .queryParam("pagina", paginaAtual)
                            .queryParam("itens", itens)
                            .build())
                    .retrieve()
                    .bodyToMono(DeputadosResponse.class)
                    .block();

            if (response == null || response.getDados().isEmpty()) {
                break;
            }

            todos.addAll(response.getDados());

            if (response.getDados().size() < itens) {
                break;
            }

            pagina++;
        }

        return todos;
    }
    public DeputadoDetalhesDTO buscarDeputadoPorId(Long id) {
        DeputadosWrapperDTO wrapper = webClient
                .get()
                .uri("/deputados/{id}", id)
                .retrieve()
                .bodyToMono(DeputadosWrapperDTO.class)
                .block();
        if (wrapper == null || wrapper.getDados() == null) {
            return null;
        }
        DeputadoDetalhesDTO dados = wrapper.getDados();
        return dados;


    }
    public List<DespesaDTO> buscarDespesas(Long deputadoId) {

        DeputadoDespesasResponseDTO response =
                webClient.get()
                        .uri("/deputados/{id}/despesas", deputadoId)
                        .retrieve()
                        .bodyToMono(DeputadoDespesasResponseDTO.class)
                        .block();

        return response != null ? response.getDados() : List.of();
    }
}
