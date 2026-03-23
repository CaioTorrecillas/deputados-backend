package com.example.deputadosbackend.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name="deputados")
public class Deputado {

    @Id
    private Long id;

    private String nome;
    private String siglaPartido;
    private String siglaUf;

    private String email;
    private Integer idLegislatura;
    private String uri;
    private String uriPartido;
    private String urlFoto;

    // 🔥 relacionamento N:N
    @ManyToMany(mappedBy = "deputados", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Proposicao> proposicoes;
}