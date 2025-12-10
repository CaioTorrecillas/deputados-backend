package com.example.deputadosbackend.Response;
import com.example.deputadosbackend.Dto.DeputadosDTO;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeputadosResponse {
    private List<DeputadosDTO> dados;

}
