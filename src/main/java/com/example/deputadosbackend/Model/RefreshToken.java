package com.example.deputadosbackend.Model;
import com.example.deputadosbackend.Model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    private String jti; // mesma string salva no JWT jti

    private String userEmail;

    private Instant expiryDate;

    // opcional: device info, ip, revoked flag, createdAt...
    private boolean revoked = false;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    // getters/setters / construtores
}