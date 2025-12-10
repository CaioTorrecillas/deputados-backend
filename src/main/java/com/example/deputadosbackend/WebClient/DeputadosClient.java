package com.example.deputadosbackend.WebClient;

import com.example.deputadosbackend.Dto.DeputadoDetalhesDTO;
import com.example.deputadosbackend.Dto.DeputadoUltimoStatusDTO;
import com.example.deputadosbackend.Dto.DeputadosDTO;
import com.example.deputadosbackend.Dto.DeputadosWrapperDTO;
import org.springframework.stereotype.Component;
import com.example.deputadosbackend.Response.DeputadosResponse;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class DeputadosClient {

    private final WebClient webClient;

    public DeputadosClient() {
        this.webClient = WebClient.builder()
                .baseUrl("https://dadosabertos.camara.leg.br/api/v2")
                .build();
    }

    public DeputadosResponse buscarDeputados() {
        return webClient.get()
                .uri("/deputados")
                .retrieve()
                .bodyToMono(DeputadosResponse.class)
                .block();


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
}
