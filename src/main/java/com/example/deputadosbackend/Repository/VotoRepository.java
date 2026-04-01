package com.example.deputadosbackend.Repository;

import com.example.deputadosbackend.Model.Voto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsByVotacaoIdAndDeputadoId(String votacaoId, Long deputadoId);

}
