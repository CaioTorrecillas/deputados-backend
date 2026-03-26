package com.example.deputadosbackend.WebClient;

import com.example.deputadosbackend.Dto.AutorDTO;
import com.example.deputadosbackend.Dto.AutoresResponseDTO;
import com.example.deputadosbackend.Dto.ProposicaoDTO;
import com.example.deputadosbackend.Dto.ProposicaoPLDetalheDTO;
import com.example.deputadosbackend.Response.ProposicaoDadosTotaisResponse;
import com.example.deputadosbackend.Response.ProposicaoDetalheResponse;
import com.example.deputadosbackend.Response.ProposicaoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
@Component
public class AtividadeClient {








    private static final Logger log = LoggerFactory.getLogger(AtividadeClient.class);
    private final WebClient webClient;
    AtividadeClient() {
        this.webClient = WebClient.builder()
                .baseUrl("https://dadosabertos.camara.leg.br/api/v2")
                .build();
    }





    public List<Map<String, Object>> buscarVotacoesPlenario25() {
        List<Map<String, Object>> todasVotacoes = new ArrayList<>();
        int pagina = 1;

        while (pagina <= 3) {
            int tentativas = 0;
            int maxTentativas = pagina == 1 ? 5 : 3;
            boolean sucesso = false;
            int paginaAtual = pagina;
            while (tentativas < maxTentativas && !sucesso) {
                try {
                    System.out.println("--------------------------------------------------");
                    System.out.println("{Proposicao Client} | Página: " + pagina + " | Tentativa: " + (tentativas + 1));

                    Map<String, Object> response = webClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/votacoes")
                                    .queryParam("itens", 100)
                                    .queryParam("pagina", paginaAtual)
                                    .queryParam("dataFim", "2025-12-31")
                                    .build())
                            .retrieve()
                            .bodyToMono(Map.class)
                            .block();

                    System.out.println("{Proposicao Client} | ✅ Resposta recebida página " + pagina);

                    if (response == null || !response.containsKey("dados")) {
                        System.out.println("{Proposicao Client} | ⚠️ Resposta inválida");
                        break;
                    }

                    List<Map<String, Object>> dados = (List<Map<String, Object>>) response.get("dados");
                    List<Map<String, Object>> links = (List<Map<String, Object>>) response.get("links");

                    int quantidade = dados != null ? dados.size() : 0;
                    System.out.println("{Proposicao Client} | 📦 Itens recebidos: " + quantidade);

                    if (dados != null) {
                        dados.stream()
                                .filter(this::isPlenario)
                                .forEach(todasVotacoes::add);
                    }

                    boolean temProxima = links != null && links.stream()
                            .anyMatch(link -> "next".equals(link.get("rel")));

                    System.out.println("{Proposicao Client} | 🔄 Tem próxima? " + temProxima);

                    sucesso = true; // deu certo 🎯

                    if (!temProxima) {
                        System.out.println("{Proposicao Client} | 🚫 Fim da paginação");
                        pagina = 999;
                    } else {
                        pagina++;
                    }

                } catch (Exception e) {
                    tentativas++;

                    System.out.println("{Proposicao Client} | ❌ Erro na página " + pagina +
                            " tentativa " + tentativas + ": " + e.getMessage());

                    if (tentativas >= maxTentativas) {
                        System.out.println("{Proposicao Client} | ⛔ Pulando página " + pagina);
                        pagina++; // pula página ruim
                        break;
                    }

                    try {
                        int espera = 3000 * tentativas;
                        System.out.println("{Proposicao Client} | ⏳ Aguardando " + espera + "ms para retry...");
                        Thread.sleep(espera);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        System.out.println("==================================================");
        System.out.println("{Proposicao Client} | Total final: " + todasVotacoes.size());
        return todasVotacoes;
    }


    public List<Map<String, Object>> buscarVotosPorVotacao(String votacaoId) {
        String uri = "/votacoes/" + votacaoId + "/votos";
        Map<String, Object> response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || !response.containsKey("dados")) {
            return Collections.emptyList();
        }

        return (List<Map<String, Object>>) response.get("dados");
    }

    // 🔹 Filtros
    private boolean isPL(Map<String, Object> item) {
        String proposicao = (String) item.get("proposicaoObjeto");
        return proposicao != null && proposicao.startsWith("PL");
    }

    private boolean isPlenario(Map<String, Object> votacao) {
        String orgao = (String) votacao.get("siglaOrgao");
        return "PLEN".equals(orgao);
    }


}