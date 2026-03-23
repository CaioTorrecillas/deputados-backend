package com.example.deputadosbackend.Repository;

import com.example.deputadosbackend.Model.Proposicao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposicaoRepository extends JpaRepository<Proposicao, Long> {
    List<Proposicao> findByAnoOrderByDataApresentacaoDesc(Integer ano);
    List<Proposicao> findTop200ByDeputadosIsEmpty();
    Page<Proposicao> findByDeputados_Id(Long idDeputado, Pageable pageable);
    Page<Proposicao> findByDeputados_IdAndSiglaTipo(
            Long idDeputado,
            String siglaTipo,
            Pageable pageable
    );
}
