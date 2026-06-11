package com.bicicletas.bicicletas.controller;
import com.bicicletas.bicicletas.dto.response.AlquilerResponse;

import com.bicicletas.bicicletas.dto.request.BicicletaRequest;
import com.bicicletas.bicicletas.dto.response.BicicletaResponse;
import com.bicicletas.bicicletas.model.entity.Bicicleta;
import com.bicicletas.bicicletas.model.enums.TipoBicicleta;
import com.bicicletas.bicicletas.service.AlquilerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bicicletas")
public class BicicletaController {

    private final AlquilerService service;

    public BicicletaController(AlquilerService service) {
        this.service = service;
    }

    // RF-01: Registrar bicicleta
    @PostMapping
    public ResponseEntity<BicicletaResponse> registrar(
            @Valid @RequestBody BicicletaRequest request) {

        Bicicleta bicicleta = new Bicicleta();
        bicicleta.setCodigo(request.getCodigo());
        bicicleta.setTipo(request.getTipo());
        bicicleta.setEstado(request.getEstado());

        Bicicleta guardada = service.registrarBicicleta(bicicleta);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(BicicletaResponse.desde(guardada));
    }

    // RF-04: Consultar disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<BicicletaResponse>> disponibles(
            @RequestParam(required = false) TipoBicicleta tipo) {

        List<BicicletaResponse> resultado = service
                .consultarDisponibles(tipo)
                .stream()
                .map(BicicletaResponse::desde)
                .toList();

        return ResponseEntity.ok(resultado);
    }

    // RF-05: Historial
    @GetMapping("/{codigo}/historial")
    public ResponseEntity<List<AlquilerResponse>> historial(
            @PathVariable String codigo) {

        List<AlquilerResponse> resultado = service
                .historialPorBicicleta(codigo)
                .stream()
                .map(AlquilerResponse::desde)
                .toList();

        return ResponseEntity.ok(resultado);
    }
}