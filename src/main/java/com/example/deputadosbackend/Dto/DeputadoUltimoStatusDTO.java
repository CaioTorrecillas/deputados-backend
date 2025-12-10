package com.example.deputadosbackend.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeputadoUltimoStatusDTO {
    private Long id;
    private String nome;
    private String siglaPartido;
    private String siglaUf;
    private String urlFoto;
    private String nomeEleitoral;
    private GabineteDTO gabinete;
    private String situacao;
    private String condicaoEleitoral;
}
