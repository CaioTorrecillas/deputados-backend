package com.example.deputadosbackend.Dto;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AutorDTO {

    private Integer id; // 🔥 ESSENCIAL (id do deputado)

    private String nome;

    private String tipo; // Ex: "Deputado", "Senador"

    private String uri;
}