package com.example.deputadosbackend.Repository;

import com.example.deputadosbackend.Model.Proposicao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposicaoRepository extends JpaRepository<Proposicao, Long> {
    List<Proposicao> findByAnoOrderByDataApresentacaoDesc(Integer ano);
}
