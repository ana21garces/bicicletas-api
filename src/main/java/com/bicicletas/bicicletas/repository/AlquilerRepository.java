package com.bicicletas.bicicletas.repository;

import com.bicicletas.bicicletas.model.entity.Alquiler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlquilerRepository extends JpaRepository<Alquiler, Long> {

    List<Alquiler> findByBicicletaCodigoOrderByHoraInicioDesc(String codigo);

    Optional<Alquiler> findByIdAndHoraFinIsNull(Long id);
}