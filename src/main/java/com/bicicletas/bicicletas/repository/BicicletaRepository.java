package com.bicicletas.bicicletas.repository;

import com.bicicletas.bicicletas.model.entity.Bicicleta;
import com.bicicletas.bicicletas.model.enums.EstadoBicicleta;
import com.bicicletas.bicicletas.model.enums.TipoBicicleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BicicletaRepository extends JpaRepository<Bicicleta, String> {

    List<Bicicleta> findByEstado(EstadoBicicleta estado);

    List<Bicicleta> findByEstadoAndTipo(EstadoBicicleta estado, TipoBicicleta tipo);
}