package com.example.deputadosbackend.Controller;

import com.example.deputadosbackend.Dto.AtividadeDTO;
import com.example.deputadosbackend.Service.AtividadeService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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


        @GetMapping("/salvar")
        public ResponseEntity<?> salvarAtividadesDeputado(//@PathVariable Long id
        ){
            Map<String, Object> resultado = atividadeService.salvarAtividadesVotacoes();
            return ResponseEntity.ok(resultado);

        }


}
