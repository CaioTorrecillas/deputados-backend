package com.example.deputadosbackend.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
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
    @ElementCollection
    @CollectionTable(
            name = "user_favorite_deputados",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "deputado_id")
    private Set<Long> favoriteDeputados = new HashSet<>();
    @Transient
    private String senha;


}