package com.example.deputadosbackend.Repository;

import com.example.deputadosbackend.Model.Atividade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtividadeRepository  extends JpaRepository<Atividade, Long> {

    boolean existsByReferenciaIdAndDeputadoId(String votacaoId, Long deputadoId );
    List<Atividade> findByDeputadoIdOrderByDataAtividadeDesc(Long deputadoId);
}
