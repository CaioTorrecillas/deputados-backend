package com.example.deputadosbackend.WebClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
@Component
public class HuggingFaceClient {
    private static final Logger log = LoggerFactory.getLogger(HuggingFaceClient.class);
    private final WebClient webClient;


    private final String token;

    public HuggingFaceClient(@Value("senhalegal") String token) {
        this.token = token;
        this.webClient =  WebClient.builder()
                .baseUrl("https://router.huggingface.co")
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }


    public String gerarResumo(String texto) {

        Map<String, String> body = new HashMap<>();
        body.put("inputs", texto);

        log.info("Enviando requisição ao Hugging Face com token: {}", token != null ? "*****" : "NULL");
        log.info("Texto enviado: {}", texto);
        try {
            String resposta = webClient
                    .post()
                    .uri("/sshleifer/distilbart-cnn-12-6")
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .exchangeToMono(response -> handleResponse(response))
                    .block();

            log.info("Resposta recebida: {}", resposta);
            return resposta;
        } catch (Exception e) {
            log.error("Erro ao chamar Hugging Face", e);
            return null;
        }
    }
    private Mono<String> handleResponse(ClientResponse response) {
        log.info("Status code da resposta: {}", response.statusCode());
        return response.bodyToMono(String.class);
    }
}