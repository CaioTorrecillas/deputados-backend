package com.example.deputadosbackend.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserDTO {

    private String nome;
    private String sobrenome;
    private String email;
    private Integer idade;
    private String localidade;
}
