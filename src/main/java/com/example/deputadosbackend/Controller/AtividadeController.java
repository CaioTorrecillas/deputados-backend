package com.example.deputadosbackend.Controller;

import com.example.deputadosbackend.Dto.AtividadeDTO;
import com.example.deputadosbackend.Service.AtividadeService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/atividade")
public class AtividadeController {
    private AtividadeService atividadeService;

    public AtividadeController(AtividadeService atividadeService){
        this.atividadeService  = atividadeService;

    }




        //Salva votações que foram realizadas no plenario e que sao de 2025. Acessa a pagina 1 a 3 da api da camara.
        @PostMapping("/sincronizar-votacoes-25")
    public ResponseEntity<?> salvarVotacoes() {
        Map<String, Object> resultado = atividadeService.salvarVotacoes();
        return ResponseEntity.ok(resultado);
    }

    //Salva votos baseados em votações que só existem no banco
    @PostMapping("/sincronizar-votos")
    public ResponseEntity<?> salvarVotos() {
        Map<String, Object> resultado = atividadeService.salvarVotos();
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/gerar")
    public ResponseEntity<?> gerarAtividades() {
        return ResponseEntity.ok(atividadeService.gerarAtividades());
    }


}
