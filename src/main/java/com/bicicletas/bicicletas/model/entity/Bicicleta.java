package com.bicicletas.bicicletas.model.entity;

import com.bicicletas.bicicletas.model.enums.EstadoBicicleta;
import com.bicicletas.bicicletas.model.enums.TipoBicicleta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bicicletas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bicicleta {

    @Id
    @Column(unique = true, nullable = false)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoBicicleta tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoBicicleta estado;

    @OneToMany(mappedBy = "bicicleta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Alquiler> alquileres = new ArrayList<>();
}