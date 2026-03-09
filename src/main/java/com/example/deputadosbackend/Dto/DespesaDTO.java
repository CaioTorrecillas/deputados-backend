package com.example.deputadosbackend.Dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class DespesaDTO {

    private Integer ano;
    private Integer mes;

    private String tipoDespesa;

    private String codDocumento;
    private String tipoDocumento;
    private Integer codTipoDocumento;

    private LocalDate dataDocumento;
    private String numDocumento;

    private BigDecimal valorDocumento;
    private BigDecimal valorLiquido;

    private String nomeFornecedor;
    private String cnpjCpfFornecedor;

    private String urlDocumento;
    private String parcela;
    // getters e setters
}