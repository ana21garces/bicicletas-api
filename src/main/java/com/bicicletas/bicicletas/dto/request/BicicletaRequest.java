package com.bicicletas.bicicletas.dto.request;

import com.bicicletas.bicicletas.model.enums.EstadoBicicleta;
import com.bicicletas.bicicletas.model.enums.TipoBicicleta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BicicletaRequest {

    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotNull(message = "El tipo es obligatorio")
    private TipoBicicleta tipo;

    @NotNull(message = "El estado es obligatorio")
    private EstadoBicicleta estado;
}