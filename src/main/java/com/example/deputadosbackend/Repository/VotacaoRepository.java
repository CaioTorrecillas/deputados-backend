package com.example.deputadosbackend.Repository;

import com.example.deputadosbackend.Model.Votacao;
import org.hibernate.boot.models.JpaAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotacaoRepository extends JpaRepository<Votacao, String> {
}
