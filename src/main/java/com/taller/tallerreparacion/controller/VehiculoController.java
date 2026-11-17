
package com.taller.tallerreparacion.controller;

import com.taller.tallerreparacion.entity.Vehiculo;
import com.taller.tallerreparacion.repository.VehiculoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.taller.tallerreparacion.entity.EstadoReparacion;
import org.springframework.transaction.annotation.Transactional;
import com.taller.tallerreparacion.entity.Averia;
import com.taller.tallerreparacion.repository.AveriaRepository;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoRepository vehiculoRepository;
    private final AveriaRepository averiaRepository;

    public VehiculoController(VehiculoRepository vehiculoRepository,
                              AveriaRepository averiaRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.averiaRepository = averiaRepository;
    }


    @GetMapping
    public ResponseEntity<List<Vehiculo>> listarVehiculos() {
        List<Vehiculo> vehiculos = vehiculoRepository.findAll();
        return ResponseEntity.ok(vehiculos);
    }

    @PostMapping
    public ResponseEntity<Vehiculo> crearVehiculo(@RequestBody Vehiculo vehiculo) {
        Vehiculo nuevoVehiculo = vehiculoRepository.save(vehiculo);
        return ResponseEntity.status(201).body(nuevoVehiculo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehiculo> obtenerVehiculo(@PathVariable Long id) {
        return vehiculoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehiculo> modificarVehiculo(
            @PathVariable Long id,
            @RequestBody Vehiculo datosVehiculo) {

        return vehiculoRepository.findById(id)
                .map(vehiculo -> {

                    vehiculo.setMatricula(datosVehiculo.getMatricula());
                    vehiculo.setModelo(datosVehiculo.getModelo());
                    vehiculo.setFechaMatriculacion(datosVehiculo.getFechaMatriculacion());

                    Vehiculo actualizado = vehiculoRepository.save(vehiculo);

                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable Long id) {

        return vehiculoRepository.findById(id)
                .map(vehiculo -> {

                    boolean tieneAveriasSinTerminar = vehiculo.getAverias()
                            .stream()
                            .anyMatch(averia ->
                                    averia.getEstado() != EstadoReparacion.TERMINADO);

                    if (tieneAveriasSinTerminar) {
                        return ResponseEntity
                                .status(409)
                                .<Void>build();
                    }

                    vehiculoRepository.delete(vehiculo);

                    return ResponseEntity
                            .noContent()
                            .<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/averias")
    public ResponseEntity<List<Averia>> obtenerAveriasVehiculo(
            @PathVariable Long id) {

        if (!vehiculoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        List<Averia> averias = averiaRepository.findByVehiculoId(id);

        return ResponseEntity.ok(averias);
    }

}
