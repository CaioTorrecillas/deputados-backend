package com.example.deputadosbackend.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PageResponseDTO<T> {

    private List<T> dados;

    private int pagina;

    private int itensPorPagina;

    private int totalItens;
}