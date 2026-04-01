package com.example.deputadosbackend.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "voto",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"votacao_id", "deputado_id"})
        })
@Getter
@Setter
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 FK lógica para votação
    @Column(name = "votacao_id", nullable = false)
    private String votacaoId;

    // 🔗 FK lógica para deputado
    @Column(name = "deputado_id", nullable = false)
    private Long deputadoId;

    // ✔ Sim, Não, Abstenção, etc
    @Column(name = "tipo_voto", nullable = false)
    private String tipoVoto;

    // 📅 quando o voto foi registrado
    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro;



    // 🕒 controle interno
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }

    // getters e setters
}