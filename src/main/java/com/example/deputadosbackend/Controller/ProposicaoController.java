
package com.example.deputadosbackend.Controller;

import com.example.deputadosbackend.Dto.*;
import com.example.deputadosbackend.Response.DeputadosResponse;
import com.example.deputadosbackend.Response.ProposicaoResponse;
import com.example.deputadosbackend.Service.ProposicaoService;
import com.example.deputadosbackend.WebClient.ProposicaoClient;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/proposicao")
public class ProposicaoController {


    private final ProposicaoService proposicaoService;

    public ProposicaoController(ProposicaoService proposicaoService) {
        this.proposicaoService = proposicaoService;
    }



    @GetMapping("/{idDeputado}/proposicoes")
    public PageResponseDTO<ProposicaoDTO> listarProposicoes(
            @PathVariable Long idDeputado,
            @RequestParam(defaultValue = "1") int pagina,
            @RequestParam(required = false) String tipo
    ) {
        return proposicaoService
                .listarProposicoesDeputado(idDeputado, pagina, tipo);
    }
    @GetMapping("/resumo/{idProposicao}")
    public ResponseEntity<String> gerarResumo(@PathVariable Long idProposicao) {

        String resumo = proposicaoService.gerarResumoProposicao(idProposicao);

        return ResponseEntity.ok(resumo);
    }
    @PostMapping("/sincronizar-projetolei")
    public ResponseEntity<SyncResponseDTO> sincronizarPL() {

        SyncResponseDTO resultado =  proposicaoService.sincronizarProjetosDeLei();

        return ResponseEntity.ok(resultado);

    }

    @PostMapping("/vincular-autores")
    public ResponseEntity<String> vincularAutores() {
        proposicaoService.vincularAutoresProposicoes();
        return ResponseEntity.ok("Autores vinculados com sucesso");
    }


    @GetMapping("/detalhes/{id}")
    public ProposicaoPLDetalheDTO getProposicaoDetalhes(@PathVariable Long id) {
        return proposicaoService.buscarProposicaoDetalhe(id);
    }




    @GetMapping("/{idDeputado}/proposicoes/dadosTotais")
    public ProposicoesDadosTotaisDTO listarProposicoesDadosTotais(
            @PathVariable Long idDeputado

    ) {
        return proposicaoService
                .listarProposicoesDeputadoDadosTotais(idDeputado);
    }

}

