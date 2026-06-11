package com.bicicletas.bicicletas.dto.response;

import com.bicicletas.bicicletas.model.entity.Alquiler;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlquilerResponse {

    private Long id;
    private String codigoBicicleta;
    private String nombreCliente;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    private Integer duracionEstimadaHoras;
    private Long duracionRealHoras;
    private Double costoTotal;
    private Boolean tuvoMulta;

    public static AlquilerResponse desde(Alquiler a) {
        AlquilerResponse dto = new AlquilerResponse();
        dto.setId(a.getId());
        dto.setCodigoBicicleta(a.getBicicleta().getCodigo());
        dto.setNombreCliente(a.getNombreCliente());
        dto.setHoraInicio(a.getHoraInicio());
        dto.setHoraFin(a.getHoraFin());
        dto.setDuracionEstimadaHoras(a.getDuracionEstimadaHoras());
        dto.setCostoTotal(a.getCostoTotal());
        dto.setTuvoMulta(a.getTuvoMulta());

        // Calcular duración real si ya finalizó
        if (a.getHoraFin() != null) {
            long minutos = java.time.temporal.ChronoUnit.MINUTES
                    .between(a.getHoraInicio(), a.getHoraFin());
            dto.setDuracionRealHoras((long) Math.ceil(minutos / 60.0));
        }

        return dto;
    }
}
