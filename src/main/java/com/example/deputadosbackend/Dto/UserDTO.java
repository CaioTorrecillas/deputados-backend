package com.example.deputadosbackend.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserDTO {
    private Long id;
    private String nome;
    private String sobrenome;
    private String email;
    private Integer idade;
    private String cidade;

    private String estado;
    private String cep;
    private String cpf;

    public UserDTO(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }
    public UserDTO( Long id, String nome, String sobrenome , String email, String cidade, String cpf, String estado ) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.sobrenome = sobrenome;
        this.cidade = cidade;
        this.cpf = cpf;
        this.estado = estado;
    }
    public UserDTO(){

    }
}
