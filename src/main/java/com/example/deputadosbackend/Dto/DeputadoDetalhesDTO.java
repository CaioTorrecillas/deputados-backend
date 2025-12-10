package com.example.deputadosbackend.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter@Getter
public class DeputadoDetalhesDTO {
    private Long id;
    private String nomeCivil;
    private String cpf;
    private String sexo;
    private String urlWebsite;
    private List<String> redeSocial;
    private String dataNascimento;
    private String dataFalecimento;
    private String ufNascimento;
    private String municipioNascimento;
    private String escolaridade;
    private DeputadoUltimoStatusDTO ultimoStatus; // composição
}
