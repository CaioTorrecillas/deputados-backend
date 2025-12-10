package com.example.deputadosbackend.Service;

import com.example.deputadosbackend.Dto.DeputadoDetalhesDTO;
import com.example.deputadosbackend.Dto.DeputadosDTO;
import com.example.deputadosbackend.Response.DeputadosResponse;
import com.example.deputadosbackend.WebClient.DeputadosClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeputadosService {

    private final DeputadosClient deputadosClient;

    public DeputadosService(DeputadosClient deputadosClient) {
        this.deputadosClient = deputadosClient;
    }

    public DeputadosResponse listarDeputados() {
        return deputadosClient.buscarDeputados();

    }


    public DeputadoDetalhesDTO buscarDeputadoPorId(Long id) {
        return deputadosClient.buscarDeputadoPorId(id);
    }

}
