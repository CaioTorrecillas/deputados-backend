package com.example.deputadosbackend.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Votacao {

    @Id
    private String id;

    private LocalDate data;

    private LocalDateTime dataHoraRegistro;

    private String siglaOrgao;

    private String descricao;

    private String uri;

    private Boolean aprovacao;

    private String proposicaoObjeto;

    private String uriEvento;

    // opcional (nível avançado)
    private Integer votosSim;
    private Integer votosNao;
    private Integer totalVotos;
}

