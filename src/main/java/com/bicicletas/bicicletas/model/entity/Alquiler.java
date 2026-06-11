package com.bicicletas.bicicletas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "alquileres")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alquiler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bicicleta_codigo", nullable = false)
    private Bicicleta bicicleta;

    @Column(nullable = false)
    private String nombreCliente;

    @Column(nullable = false)
    private LocalDateTime horaInicio;

    @Column(nullable = false)
    private Integer duracionEstimadaHoras;

    private LocalDateTime horaFin;

    private Double costoTotal;

    private Boolean tuvoMulta = false;

}