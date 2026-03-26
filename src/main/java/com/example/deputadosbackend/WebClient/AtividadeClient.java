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

        while (pagina <= 1) {
            int paginaAtual = pagina;
            System.out.println("{Proposicao Client} | Chamando API: /votacoes?pagina=" + paginaAtual);

            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/votacoes")
                            .queryParam("itens", 100)
                            .queryParam("pagina", paginaAtual)
                            .queryParam("dataFim", "2025-12-31")
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(Duration.ofMinutes(5)); // espera até 5 minutos antes de dar timeout

            if (response == null || !response.containsKey("dados")) {
                break;
            }

            List<Map<String, Object>> dados = (List<Map<String, Object>>) response.get("dados");
            List<Map<String, Object>> links = (List<Map<String, Object>>) response.get("links");

            if (dados != null) {
                // 🔹 filtrar apenas votações do plenário
                dados.stream()
                        .filter(this::isPlenario)
                        .forEach(todasVotacoes::add);
            }

            boolean temProxima = links != null && links.stream()
                    .anyMatch(link -> "next".equals(link.get("rel")));

            System.out.println("{Proposicao Client} | Página " + pagina + " processada, próxima? " + temProxima);

            if (!temProxima) {
                break;
            }

            pagina++;
        }

        System.out.println("{Proposicao Client} | Todas as páginas processadas. Total de votações filtradas: " + todasVotacoes.size());
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