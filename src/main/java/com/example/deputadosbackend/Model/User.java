package com.example.deputadosbackend.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
//import org.springframework.data.annotation.Id;

@Getter
@Setter
@Entity
@Table(name = "usuarios")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String sobrenome;
    @Column(unique = true)
    private String email;
    private String senhaHash;
    private String estado;
    private String cpf;
    private String cidade;

    @Transient
    private String senha;


}