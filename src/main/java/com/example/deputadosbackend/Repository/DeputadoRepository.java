package com.example.deputadosbackend.Repository;

import com.example.deputadosbackend.Dto.DeputadosDTO;
import com.example.deputadosbackend.Model.Deputado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeputadoRepository extends JpaRepository<Deputado, Long> {
    Optional<Deputado> findByNome(String nome);
}
