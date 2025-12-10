package com.example.deputadosbackend.Controller;

import com.example.deputadosbackend.Dto.DeputadoDetalhesDTO;
import com.example.deputadosbackend.Dto.DeputadosDTO;
import com.example.deputadosbackend.Response.DeputadosResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.deputadosbackend.Service.DeputadosService;

import java.util.Map;

@RestController
@RequestMapping("/deputados")
public class DeputadosController {

    private final DeputadosService deputadosService;

    public DeputadosController(DeputadosService deputadoService) {
        this.deputadosService = deputadoService;
    }

    @GetMapping
    public DeputadosResponse getDeputados() {
        return deputadosService.listarDeputados();
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarDeputadoPorId(@PathVariable Long id) {
        DeputadoDetalhesDTO deputado = deputadosService.buscarDeputadoPorId(id);
        if (deputado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deputado);
        //ResponseEntity.status(200).body(Map.of("message", "Login realizado"));
    }
}
