package com.example.deputadosbackend.Response;

import com.example.deputadosbackend.Dto.LinkDTO;

import java.util.List;

public class ProposicaoDadosTotaisResponse<T> {

    private List<T> dados;
    private List<LinkDTO> links;

    public List<T> getDados() {
        return dados;
    }

    public void setDados(List<T> dados) {
        this.dados = dados;
    }

    public List<LinkDTO> getLinks() {
        return links;
    }

    public void setLinks(List<LinkDTO> links) {
        this.links = links;
    }
}