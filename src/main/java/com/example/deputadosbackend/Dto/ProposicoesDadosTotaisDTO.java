package com.example.deputadosbackend.Dto;

import java.util.Map;

public class ProposicoesDadosTotaisDTO{
    private int total;

    private Map<String, Long> porTipo;

    public ProposicoesDadosTotaisDTO(int total, Map<String, Long> porTipo) {
        this.total = total;
        this.porTipo = porTipo;
    }

    public int getTotal() {
        return total;
    }

    public Map<String, Long> getPorTipo() {
        return porTipo;
    }
}