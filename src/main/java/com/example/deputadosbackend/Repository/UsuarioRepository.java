package com.example.deputadosbackend.Repository;

import com.example.deputadosbackend.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface  UsuarioRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
