package com.example.deputadosbackend.Dto;


import com.example.deputadosbackend.Dto.StatusProposicaoDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProposicaoPLDetalheDTO {

    private Long id;

    private String siglaTipo;        // PL
    private String descricaoTipo;    // Projeto de Lei
    private Integer numero;
    private Integer ano;

    private String ementa;
    private LocalDateTime dataApresentacao;

    private StatusProposicaoDTO statusProposicao;

    private String urlInteiroTeor;

    // getters e setters
}
