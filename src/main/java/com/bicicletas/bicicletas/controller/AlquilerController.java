package com.bicicletas.bicicletas.controller;

import com.bicicletas.bicicletas.dto.request.AlquilerRequest;
import com.bicicletas.bicicletas.dto.response.AlquilerResponse;
import com.bicicletas.bicicletas.model.entity.Alquiler;
import com.bicicletas.bicicletas.service.AlquilerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alquileres")
public class AlquilerController {

    private final AlquilerService service;

    public AlquilerController(AlquilerService service) {
        this.service = service;
    }

    // RF-02: Iniciar alquiler
    @PostMapping
    public ResponseEntity<AlquilerResponse> iniciar(
            @Valid @RequestBody AlquilerRequest request) {

        Alquiler alquiler = service.iniciarAlquiler(
                request.getCodigoBicicleta(),
                request.getNombreCliente(),
                request.getDuracionEstimadaHoras()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AlquilerResponse.desde(alquiler));
    }

    // RF-03: Finalizar alquiler
    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<AlquilerResponse> finalizar(
            @PathVariable Long id) {

        Alquiler alquiler = service.finalizarAlquiler(id);
        return ResponseEntity.ok(AlquilerResponse.desde(alquiler));
    }
}