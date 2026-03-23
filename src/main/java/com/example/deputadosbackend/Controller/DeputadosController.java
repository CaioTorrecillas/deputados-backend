package com.example.deputadosbackend.Controller;

import com.example.deputadosbackend.Dto.*;
import com.example.deputadosbackend.Model.Deputado;
import com.example.deputadosbackend.Response.DeputadosResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.deputadosbackend.Service.DeputadosService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/deputados")
public class DeputadosController {

    private final DeputadosService deputadosService;

    public DeputadosController(DeputadosService deputadoService) {
        this.deputadosService = deputadoService;
    }

    @GetMapping
    public ResponseEntity<List<Deputado>> buscarTodosDeputados() {
        List<Deputado> deputados = deputadosService.listarDeputados();
        return ResponseEntity.ok(deputados);
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


    @GetMapping("/{id}/despesas")
    public ResponseEntity<List<DespesaDTO>> listarDespesas(@PathVariable Long id) {
        return ResponseEntity.ok(deputadosService.buscarDespesas(id));
    }

    @PostMapping("/sincronizar-deputados")
    public ResponseEntity<SyncDeputadosResponseDTO> sincronizarDeputados() {
        return ResponseEntity.ok(deputadosService.sincronizarDeputados());
    }
}
