package com.example.deputadosbackend.Model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Deputado {

    private String email;
    private Integer id;
    private Integer idLegislatura;
    private String nome;
    private String siglaPartido;
    private String siglaUf;
    private String uri;
    private String uriPartido;
    private String urlFoto;
}
