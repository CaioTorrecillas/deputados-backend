package com.example.deputadosbackend.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "atividade")
@Getter
@Setter
public class Atividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK para deputado (você pode ter uma entidade Deputado)
    @Column(name = "deputado_id", nullable = false)
    private Long deputadoId;

    // Tipo de atividade: votacao, proposicao, requerimento, etc.
    @Column(name = "tipo", length = 50, nullable = false)
    private String tipo;

    // Data em que a atividade ocorreu
    @Column(name = "data_atividade", nullable = false)
    private LocalDateTime dataAtividade;

    // Descrição ou contexto da atividade
    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    // Opcional: ID da votação ou proposição associada
    @Column(name = "referencia_id", length = 100)
    private String referenciaId;

    // Opcional: URI da votação ou proposição (para consulta futura)
    @Column(name = "referencia_uri", columnDefinition = "TEXT")
    private String referenciaUri;

    // Data de criação no banco
    @Column(name = "criado_banco_em", nullable = false, updatable = false)
    private LocalDateTime criadoNoBancoEm = LocalDateTime.now();

    // Data de última atualização
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    // getters e setters
}