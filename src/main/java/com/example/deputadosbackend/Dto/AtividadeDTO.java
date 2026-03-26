package com.example.deputadosbackend.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtividadeDTO {
    private String deputado;
    private String tipoVoto;
    private String dataVotacao;
    private String votacaoId;
    private String proposicao; // pode ser null
}
