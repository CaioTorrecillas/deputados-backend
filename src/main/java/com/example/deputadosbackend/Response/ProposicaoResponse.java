package com.example.deputadosbackend.Response;

import java.util.List;

import com.example.deputadosbackend.Dto.ProposicaoDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProposicaoResponse {
    private List<ProposicaoDTO> dados;

}
