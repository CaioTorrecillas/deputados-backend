package com.example.deputadosbackend.Model;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "proposicoes")
public class Proposicao {

    @Id
    private Long id;

    private String siglaTipo;

    private Integer numero;

    private Integer ano;

    @Column(length = 2000)
    private String ementa;

    private String status;

    private String dataApresentacao;

    @Column(columnDefinition = "TEXT")
    private String resumoIa;

    @ManyToMany
    @JoinTable(
            name = "proposicao_deputado",
            joinColumns = @JoinColumn(name = "proposicao_id"),
            inverseJoinColumns = @JoinColumn(name = "deputado_id")
    )
    private List<Deputado> deputados;
    // getters e setters
}