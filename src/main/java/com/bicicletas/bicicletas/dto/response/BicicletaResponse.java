package com.bicicletas.bicicletas.dto.response;

import com.bicicletas.bicicletas.model.enums.EstadoBicicleta;
import com.bicicletas.bicicletas.model.enums.TipoBicicleta;
import com.bicicletas.bicicletas.model.entity.Bicicleta;
import lombok.Data;

@Data
public class BicicletaResponse {

    private String codigo;
    private TipoBicicleta tipo;
    private EstadoBicicleta estado;

    public static BicicletaResponse desde(Bicicleta b) {
        BicicletaResponse dto = new BicicletaResponse();
        dto.setCodigo(b.getCodigo());
        dto.setTipo(b.getTipo());
        dto.setEstado(b.getEstado());
        return dto;
    }
}