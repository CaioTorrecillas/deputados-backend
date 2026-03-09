package com.example.deputadosbackend.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProposicaoDTO {

    private Long id;
    private String siglaTipo;   // PL
    private Integer numero;     // 1234
    private Integer ano;        // 2024
    private String ementa;
    private String codTipo;
    private String dataApresentacao;

    // getters e setters
}
