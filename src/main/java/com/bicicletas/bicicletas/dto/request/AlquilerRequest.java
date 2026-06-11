package com.bicicletas.bicicletas.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AlquilerRequest {

    @NotBlank(message = "El código de la bicicleta es obligatorio")
    private String codigoBicicleta;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String nombreCliente;

    @NotNull(message = "La duración estimada es obligatoria")
    @Min(value = 1, message = "La duración mínima es 1 hora")
    private Integer duracionEstimadaHoras;
}