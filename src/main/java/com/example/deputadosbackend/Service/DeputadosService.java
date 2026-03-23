package com.example.deputadosbackend.Service;

import com.example.deputadosbackend.Dto.*;
import com.example.deputadosbackend.Model.Deputado;
import com.example.deputadosbackend.Model.Proposicao;
import com.example.deputadosbackend.Repository.DeputadoRepository;
import com.example.deputadosbackend.Response.DeputadosResponse;
import com.example.deputadosbackend.WebClient.DeputadosClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class DeputadosService {

    private final DeputadosClient deputadosClient;
    private final DeputadoRepository deputadoRepository;
    public DeputadosService(DeputadosClient deputadosClient, DeputadoRepository deputadoRepository) {
        this.deputadosClient = deputadosClient;
        this.deputadoRepository = deputadoRepository;
    }

    public List<Deputado> listarDeputados() {
        return deputadoRepository.findAll();

    }


    public DeputadoDetalhesDTO buscarDeputadoPorId(Long id) {
        return deputadosClient.buscarDeputadoPorId(id);
    }
    public List<DespesaDTO> buscarDespesas(Long deputadoId) {

        return deputadosClient.buscarDespesas(deputadoId);
    }

    public SyncDeputadosResponseDTO sincronizarDeputados() {

        List<DeputadosDTO> deputados = deputadosClient.sincronizarDeputados();

        SyncDeputadosResponseDTO resultado = new SyncDeputadosResponseDTO();

        for (DeputadosDTO dto : deputados) {

            Deputado existente = deputadoRepository
                    .findById(dto.getId())
                    .orElse(null);

            if (existente != null) {

                boolean mudou = false;


                if (!Objects.equals(existente.getNome(), dto.getNome())) {
                    existente.setNome(dto.getNome());
                    mudou = true;
                }

                if (!Objects.equals(existente.getSiglaPartido(), dto.getSiglaPartido())) {
                    existente.setSiglaPartido(dto.getSiglaPartido());
                    mudou = true;
                }

                if (!Objects.equals(existente.getSiglaUf(), dto.getSiglaUf())) {
                    existente.setSiglaUf(dto.getSiglaUf());
                    mudou = true;
                }

                if (!Objects.equals(existente.getUrlFoto(), dto.getUrlFoto())) {
                    existente.setUrlFoto(dto.getUrlFoto());
                    mudou = true;
                }
                if (!Objects.equals(existente.getEmail(), dto.getEmail())) {
                    existente.setEmail(dto.getEmail());
                    mudou = true;
                }

                if (!Objects.equals(existente.getUri(), dto.getUri())) {
                    existente.setUri(dto.getUri());
                    mudou = true;
                }

                if (!Objects.equals(existente.getUriPartido(), dto.getUriPartido())) {
                    existente.setUriPartido(dto.getUriPartido());
                    mudou = true;
                }

                if (!Objects.equals(existente.getIdLegislatura(), dto.getIdLegislatura())) {
                    existente.setIdLegislatura(dto.getIdLegislatura());
                    mudou = true;
                }
                // 💾 salva só se mudou
                if (mudou) {
                    deputadoRepository.save(existente);
                    resultado.setAtualizados(resultado.getAtualizados() + 1);
                    resultado.getIdsAtualizados().add(dto.getId());
                } else {
                    resultado.setNaoAlterados(resultado.getNaoAlterados() + 1);
                }

            } else {

                // 🆕 novo deputado
                Deputado novo = new Deputado();

                novo.setId(dto.getId());
                novo.setNome(dto.getNome());
                novo.setSiglaPartido(dto.getSiglaPartido());
                novo.setSiglaUf(dto.getSiglaUf());
                novo.setUrlFoto(dto.getUrlFoto());
                novo.setEmail(dto.getEmail());
                novo.setUri(dto.getUri());
                novo.setUriPartido(dto.getUriPartido());
                novo.setIdLegislatura(dto.getIdLegislatura());
                deputadoRepository.save(novo);

                resultado.setNovos(resultado.getNovos() + 1);
            }
        }

        return resultado;
    }
}
