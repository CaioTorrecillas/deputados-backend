package com.example.deputadosbackend.Controller;

import com.example.deputadosbackend.Dto.PageResponseDTO;
import com.example.deputadosbackend.Dto.ProposicaoDTO;
import com.example.deputadosbackend.Dto.ProposicaoPLDetalheDTO;
import com.example.deputadosbackend.Dto.ProposicoesDadosTotaisDTO;
import com.example.deputadosbackend.Response.DeputadosResponse;
import com.example.deputadosbackend.Response.ProposicaoResponse;
import com.example.deputadosbackend.Service.ProposicaoService;
import com.example.deputadosbackend.WebClient.ProposicaoClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/proposicao")
public class ProposicaoController {


    private final ProposicaoService proposicaoService;

    public ProposicaoController(ProposicaoService proposicaoService) {
        this.proposicaoService = proposicaoService;
    }

    @GetMapping("/projetos-lei")
    public ProposicaoResponse getProjetosDeLei(@RequestParam(required = false) Integer ano) {
        return proposicaoService.listarProposicoes(ano);
    }

    @GetMapping("/detalhes/{id}")
    public ProposicaoPLDetalheDTO getProposicaoDetalhes(@PathVariable Long id) {
        return proposicaoService.buscarProposicaoDetalhe(id);
    }

    @GetMapping("/{idDeputado}/proposicoes")
    public Mono<PageResponseDTO<ProposicaoDTO>> listarProposicoes(
            @PathVariable Long idDeputado,
            @RequestParam(defaultValue = "1") int pagina
    ) {
        return proposicaoService
                .listarProposicoesDeputado(idDeputado, pagina);
    }
    @GetMapping("/{idDeputado}/proposicoes/dadosTotais")
    public ProposicoesDadosTotaisDTO listarProposicoesDadosTotais(
            @PathVariable Long idDeputado

    ) {
        return proposicaoService
                .listarProposicoesDeputadoDadosTotais(idDeputado);
    }
}
