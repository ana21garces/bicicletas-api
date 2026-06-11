package com.bicicletas.bicicletas.service;

import com.bicicletas.bicicletas.model.entity.Alquiler;
import com.bicicletas.bicicletas.model.entity.Bicicleta;
import com.bicicletas.bicicletas.model.enums.EstadoBicicleta;
import com.bicicletas.bicicletas.model.enums.TipoBicicleta;
import com.bicicletas.bicicletas.repository.AlquilerRepository;
import com.bicicletas.bicicletas.repository.BicicletaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class AlquilerService {

    private final BicicletaRepository bicicletaRepository;
    private final AlquilerRepository alquilerRepository;

    // Constructor injection
    public AlquilerService(BicicletaRepository bicicletaRepository,
                           AlquilerRepository alquilerRepository) {
        this.bicicletaRepository = bicicletaRepository;
        this.alquilerRepository = alquilerRepository;
    }

    // TARIFAS
    private static final Map<TipoBicicleta, Double> TARIFAS = Map.of(
            TipoBicicleta.URBANA,    3500.0,
            TipoBicicleta.MONTANA,   5000.0,
            TipoBicicleta.ELECTRICA, 7500.0
    );

    // RF-01: REGISTRAR BICICLETA
    public Bicicleta registrarBicicleta(Bicicleta bicicleta) {
        if (bicicletaRepository.existsById(bicicleta.getCodigo())) {
            throw new IllegalArgumentException(
                    "Ya existe una bicicleta con el código: " + bicicleta.getCodigo()
            );
        }
        return bicicletaRepository.save(bicicleta);
    }

    // RF-04: CONSULTAR DISPONIBLES
    public List<Bicicleta> consultarDisponibles(TipoBicicleta tipo) {
        if (tipo != null) {
            return bicicletaRepository.findByEstadoAndTipo(
                    EstadoBicicleta.DISPONIBLE, tipo
            );
        }
        return bicicletaRepository.findByEstado(EstadoBicicleta.DISPONIBLE);
    }

    // RF-05: HISTORIAL
    public List<Alquiler> historialPorBicicleta(String codigo) {
        bicicletaRepository.findById(codigo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe bicicleta con código: " + codigo
                ));
        return alquilerRepository
                .findByBicicletaCodigoOrderByHoraInicioDesc(codigo);
    }

    // RF-02: INICIAR ALQUILER
    @Transactional
    public Alquiler iniciarAlquiler(String codigoBicicleta,
                                    String nombreCliente,
                                    Integer duracionEstimadaHoras) {
        // RN-04: verificar disponibilidad
        Bicicleta bicicleta = bicicletaRepository.findById(codigoBicicleta)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe bicicleta con código: " + codigoBicicleta
                ));

        if (bicicleta.getEstado() != EstadoBicicleta.DISPONIBLE) {
            throw new IllegalStateException(
                    "La bicicleta " + codigoBicicleta +
                            " no está disponible. Estado actual: " + bicicleta.getEstado()
            );
        }

        // Cambiar estado a ALQUILADA
        bicicleta.setEstado(EstadoBicicleta.ALQUILADA);
        bicicletaRepository.save(bicicleta);

        // Crear el alquiler
        Alquiler alquiler = new Alquiler();
        alquiler.setBicicleta(bicicleta);
        alquiler.setNombreCliente(nombreCliente);
        alquiler.setHoraInicio(LocalDateTime.now());
        alquiler.setDuracionEstimadaHoras(duracionEstimadaHoras);
        alquiler.setTuvoMulta(false);

        return alquilerRepository.save(alquiler);
    }

    // RF-03: FINALIZAR ALQUILER
    @Transactional
    public Alquiler finalizarAlquiler(Long id) {
        // RN-05: verificar que existe y no está finalizado
        Alquiler alquiler = alquilerRepository.findByIdAndHoraFinIsNull(id)
                .orElseThrow(() -> new IllegalStateException(
                        "El alquiler " + id + " no existe o ya fue finalizado."
                ));

        LocalDateTime horaFin = LocalDateTime.now();
        alquiler.setHoraFin(horaFin);

        // Calcular costo
        double costo = calcularCosto(alquiler, horaFin);
        alquiler.setCostoTotal(costo);

        // Liberar bicicleta
        Bicicleta bicicleta = alquiler.getBicicleta();
        bicicleta.setEstado(EstadoBicicleta.DISPONIBLE);
        bicicletaRepository.save(bicicleta);

        return alquilerRepository.save(alquiler);
    }

    // CÁLCULO DE COSTO (RN-01, RN-02, RN-03)
    public double calcularCosto(Alquiler alquiler, LocalDateTime horaFin) {
        TipoBicicleta tipo = alquiler.getBicicleta().getTipo();
        double tarifa = TARIFAS.get(tipo);

        // Minutos reales de uso
        long minutosReales = ChronoUnit.MINUTES.between(
                alquiler.getHoraInicio(), horaFin
        );

        // RN-02: horas reales redondeadas al alza
        long horasReales = (long) Math.ceil(minutosReales / 60.0);

        // Costo base
        double costoBase = horasReales * tarifa;

        // RN-03: calcular multa si aplica
        long minutosEstimados = alquiler.getDuracionEstimadaHoras() * 60L;
        double multa = 0.0;

        if (minutosReales > minutosEstimados) {
            long minutosRetraso = minutosReales - minutosEstimados;
            long horasRetraso = (long) Math.ceil(minutosRetraso / 60.0);
            multa = horasRetraso * (tarifa * 0.5);
            alquiler.setTuvoMulta(true);
        }

        return costoBase + multa;
    }
}