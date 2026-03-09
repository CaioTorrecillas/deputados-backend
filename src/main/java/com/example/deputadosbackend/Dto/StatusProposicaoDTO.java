package com.example.deputadosbackend.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusProposicaoDTO {

    private String descricaoSituacao;
    private String descricaoTramitacao;
    private String siglaOrgao;
    private String regime;
    private String despacho;

    // getters e setters
}