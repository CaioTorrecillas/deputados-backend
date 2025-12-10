package com.example.deputadosbackend.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeputadosDTO {
    private Long id;
    private String nome;
    private String email;
    private String siglaPartido;
    private String uri;
    private String uriPartido;
    private String urlFoto;
    private String siglaUf;
    // getters e setters
}
