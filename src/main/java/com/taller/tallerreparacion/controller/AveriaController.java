
package com.taller.tallerreparacion.controller;

import com.taller.tallerreparacion.entity.Averia;
import com.taller.tallerreparacion.repository.AveriaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.taller.tallerreparacion.entity.Vehiculo;
import com.taller.tallerreparacion.repository.VehiculoRepository;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import com.taller.tallerreparacion.entity.EstadoReparacion;

import java.util.List;

@RestController
@RequestMapping("/api/averias")
public class AveriaController {

    private final AveriaRepository averiaRepository;
    private final VehiculoRepository vehiculoRepository;

    public AveriaController(AveriaRepository averiaRepository,
                            VehiculoRepository vehiculoRepository) {
        this.averiaRepository = averiaRepository;
        this.vehiculoRepository = vehiculoRepository;
    }


    @GetMapping
    public ResponseEntity<List<Averia>> listarAverias() {
        List<Averia> averias = averiaRepository.findAll();
        return ResponseEntity.ok(averias);
    }

    @PostMapping
    public ResponseEntity<Averia> crearAveria(@RequestBody Averia averia) {

        if (averia.getVehiculo() == null ||
                averia.getVehiculo().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Long vehiculoId = averia.getVehiculo().getId();

        return vehiculoRepository.findById(vehiculoId)
                .map(vehiculo -> {


                    averia.setId(null);
                    averia.setVehiculo(vehiculo);

                    Averia nuevaAveria = averiaRepository.save(averia);

                    vehiculo.getAverias().add(nuevaAveria);


                    return ResponseEntity.status(201).body(nuevaAveria);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Averia> obtenerAveria(@PathVariable Long id) {

        return averiaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }



    @PutMapping("/{id}")
    public ResponseEntity<Averia> modificarAveria(
            @PathVariable Long id,
            @RequestBody JsonNode datos) {

        if (!datos.hasNonNull("descripcion") ||
                !datos.get("descripcion").isTextual() ||
                datos.get("descripcion").asText().isBlank() ||
                !datos.hasNonNull("estado") ||
                !datos.get("estado").isTextual()) {
            return ResponseEntity.badRequest().build();
        }

        EstadoReparacion nuevoEstado;

        try {
            nuevoEstado = EstadoReparacion.valueOf(
                    datos.get("estado").asText()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        return averiaRepository.findById(id)
                .map(averia -> {

                    averia.setDescripcion(datos.get("descripcion").asText());
                    averia.setEstado(nuevoEstado);

                    Averia actualizada = averiaRepository.save(averia);

                    return ResponseEntity.ok(actualizada);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> eliminarAveria(@PathVariable Long id) {

        return averiaRepository.findById(id)
                .map(averia -> {

                    Vehiculo vehiculo = averia.getVehiculo();

                    vehiculo.getAverias().remove(averia);

                    averiaRepository.delete(averia);

                    return ResponseEntity
                            .noContent()
                            .<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
