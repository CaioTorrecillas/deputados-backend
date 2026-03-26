package com.example.deputadosbackend.Repository;

import com.example.deputadosbackend.Model.Atividade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtividadeRepository  extends JpaRepository<Atividade, Long> {
}
