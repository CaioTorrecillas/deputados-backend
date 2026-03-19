package com.example.deputadosbackend.Model;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
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

    // getters e setters
}