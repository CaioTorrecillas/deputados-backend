package com.example.deputadosbackend.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SyncDeputadosResponseDTO {
    private int novos;
    private int atualizados;
    private int naoAlterados;

    private List<Long> idsAtualizados = new ArrayList<>();

}

