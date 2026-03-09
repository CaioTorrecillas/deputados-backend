
package com.example.deputadosbackend.Response;
import com.example.deputadosbackend.Dto.DespesaDTO;
import java.util.List;

public class DeputadoDespesasResponseDTO {
    private List<DespesaDTO> dados;

    public List<DespesaDTO> getDados() {
        return dados;
    }

    public void setDados(List<DespesaDTO> dados) {
        this.dados = dados;
    }
}
