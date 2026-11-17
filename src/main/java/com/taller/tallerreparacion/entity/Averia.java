
package com.taller.tallerreparacion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Averia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    private EstadoReparacion estado = EstadoReparacion.PENDIENTE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaUltimaActualizacion;

    @JsonIgnoreProperties(value = {"averias"}, allowSetters = true)
    @ManyToOne(optional = false)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @PrePersist
    public void antesDeGuardar() {
        fechaCreacion = LocalDateTime.now();
        fechaUltimaActualizacion = fechaCreacion;

        if (estado == null) {
            estado = EstadoReparacion.PENDIENTE;
        }
    }

    @PreUpdate
    public void antesDeActualizar() {
        fechaUltimaActualizacion = LocalDateTime.now();
    }
}
