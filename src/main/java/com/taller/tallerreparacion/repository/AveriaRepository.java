
package com.taller.tallerreparacion.repository;

import com.taller.tallerreparacion.entity.Averia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AveriaRepository extends JpaRepository<Averia, Long> {

    List<Averia> findByVehiculoId(Long vehiculoId);

}
