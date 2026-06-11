package com.bicicletas.bicicletas.config;

import com.bicicletas.bicicletas.model.entity.Bicicleta;
import com.bicicletas.bicicletas.model.enums.EstadoBicicleta;
import com.bicicletas.bicicletas.model.enums.TipoBicicleta;
import com.bicicletas.bicicletas.repository.BicicletaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final BicicletaRepository repository;

    public DataLoader(BicicletaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() == 0) {
            repository.save(new Bicicleta("BIC-001", TipoBicicleta.URBANA,    EstadoBicicleta.DISPONIBLE,      null));
            repository.save(new Bicicleta("BIC-002", TipoBicicleta.MONTANA,   EstadoBicicleta.DISPONIBLE,      null));
            repository.save(new Bicicleta("BIC-003", TipoBicicleta.ELECTRICA, EstadoBicicleta.DISPONIBLE,      null));
            repository.save(new Bicicleta("BIC-004", TipoBicicleta.MONTANA,   EstadoBicicleta.EN_MANTENIMIENTO,null));
            repository.save(new Bicicleta("BIC-005", TipoBicicleta.URBANA,    EstadoBicicleta.DISPONIBLE,      null));
            System.out.println("✓ Datos de ejemplo cargados correctamente.");
        }
    }
}