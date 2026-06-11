package com.bicicletas.bicicletas;

import com.bicicletas.bicicletas.model.entity.Alquiler;
import com.bicicletas.bicicletas.model.entity.Bicicleta;
import com.bicicletas.bicicletas.model.enums.EstadoBicicleta;
import com.bicicletas.bicicletas.model.enums.TipoBicicleta;
import com.bicicletas.bicicletas.repository.AlquilerRepository;
import com.bicicletas.bicicletas.repository.BicicletaRepository;
import com.bicicletas.bicicletas.service.AlquilerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlquilerServiceTest {

    @Mock
    private BicicletaRepository bicicletaRepository;

    @Mock
    private AlquilerRepository alquilerRepository;

    @InjectMocks
    private AlquilerService service;

    private Bicicleta bicicletaUrbana;
    private Bicicleta bicicletaMontana;
    private Bicicleta bicicletaNoDisponible;

    @BeforeEach
    void setUp() {
        bicicletaUrbana = new Bicicleta(
                "BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE, null
        );
        bicicletaMontana = new Bicicleta(
                "BIC-002", TipoBicicleta.MONTANA, EstadoBicicleta.DISPONIBLE, null
        );
        bicicletaNoDisponible = new Bicicleta(
                "BIC-004", TipoBicicleta.MONTANA, EstadoBicicleta.EN_MANTENIMIENTO, null
        );
    }

    // TEST 1: Costo base sin multa
    @Test
    void calcularCosto_sinMulta_devueltaAntesDeTiempo() {
        Alquiler alquiler = new Alquiler();
        alquiler.setBicicleta(bicicletaUrbana);
        alquiler.setHoraInicio(LocalDateTime.now().minusMinutes(70));
        alquiler.setDuracionEstimadaHoras(2);

        // 70 minutos reales → ceil(70/60) = 2 horas → 2 × 3500 = 7000
        double costo = service.calcularCosto(alquiler, LocalDateTime.now());

        assertEquals(7000.0, costo);
        assertFalse(alquiler.getTuvoMulta());
    }

    // ─── TEST 2: Costo exacto en la hora estimada ────────────────────────────
    @Test
    void calcularCosto_sinMulta_devueltaExactamente() {
        Alquiler alquiler = new Alquiler();
        alquiler.setBicicleta(bicicletaUrbana);
        alquiler.setHoraInicio(LocalDateTime.now().minusHours(2));
        alquiler.setDuracionEstimadaHoras(2);

        // 120 minutos exactos → 2 horas → 2 × 3500 = 7000, sin multa
        double costo = service.calcularCosto(alquiler, LocalDateTime.now());

        assertEquals(7000.0, costo);
        assertFalse(alquiler.getTuvoMulta());
    }

    //  TEST 3: Costo con multa
    @Test
    void calcularCosto_conMulta_ejemploDelEnunciado() {
        Alquiler alquiler = new Alquiler();
        alquiler.setBicicleta(bicicletaMontana);
        alquiler.setHoraInicio(LocalDateTime.now().minusMinutes(200));
        alquiler.setDuracionEstimadaHoras(2);

        // 200 min reales → ceil(200/60) = 4h × 5000 = 20000
        // retraso: 200 - 120 = 80 min → ceil(80/60) = 2h × 2500 = 5000
        // total: 25000
        double costo = service.calcularCosto(alquiler, LocalDateTime.now());

        assertEquals(25000.0, costo);
        assertTrue(alquiler.getTuvoMulta());
    }

    // TEST 4: Costo bicicleta eléctrica
    @Test
    void calcularCosto_bicicletaElectrica_tarifaCorrecta() {
        Bicicleta electrica = new Bicicleta(
                "BIC-003", TipoBicicleta.ELECTRICA, EstadoBicicleta.DISPONIBLE, null
        );
        Alquiler alquiler = new Alquiler();
        alquiler.setBicicleta(electrica);
        alquiler.setHoraInicio(LocalDateTime.now().minusHours(1));
        alquiler.setDuracionEstimadaHoras(2);

        // 60 min → 1 hora → 1 × 7500 = 7500
        double costo = service.calcularCosto(alquiler, LocalDateTime.now());

        assertEquals(7500.0, costo);
        assertFalse(alquiler.getTuvoMulta());
    }

    // TEST 5: No se puede alquilar bicicleta no disponible
    @Test
    void iniciarAlquiler_bicicletaNoDisponible_lanzaExcepcion() {
        when(bicicletaRepository.findById("BIC-004"))
                .thenReturn(Optional.of(bicicletaNoDisponible));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.iniciarAlquiler("BIC-004", "Ana Garces", 2)
        );

        assertTrue(ex.getMessage().contains("no está disponible"));
        verify(alquilerRepository, never()).save(any());
    }

    // TEST 6: No se puede finalizar alquiler ya finalizado
    @Test
    void finalizarAlquiler_yaFinalizado_lanzaExcepcion() {
        when(alquilerRepository.findByIdAndHoraFinIsNull(99L))
                .thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.finalizarAlquiler(99L)
        );

        assertTrue(ex.getMessage().contains("no existe o ya fue finalizado"));
    }

    //  TEST 7: Iniciar alquiler exitoso
    @Test
    void iniciarAlquiler_exitoso_cambiaBicicletaAAlquilada() {
        when(bicicletaRepository.findById("BIC-001"))
                .thenReturn(Optional.of(bicicletaUrbana));
        when(bicicletaRepository.save(any())).thenReturn(bicicletaUrbana);
        when(alquilerRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Alquiler resultado = service.iniciarAlquiler("BIC-001", "Ana Garces", 2);

        assertEquals(EstadoBicicleta.ALQUILADA, bicicletaUrbana.getEstado());
        assertEquals("Ana Garces", resultado.getNombreCliente());
        assertNull(resultado.getHoraFin());
        verify(alquilerRepository, times(1)).save(any());
    }

    // TEST 8: Disponibles filtra correctamente
    @Test
    void consultarDisponibles_sinFiltro_devuelveSoloDisponibles() {
        when(bicicletaRepository.findByEstado(EstadoBicicleta.DISPONIBLE))
                .thenReturn(List.of(bicicletaUrbana, bicicletaMontana));

        List<Bicicleta> resultado = service.consultarDisponibles(null);

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream()
                .allMatch(b -> b.getEstado() == EstadoBicicleta.DISPONIBLE));
    }
}