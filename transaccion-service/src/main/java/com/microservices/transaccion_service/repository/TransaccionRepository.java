package com.microservices.transaccion_service.repository;

import com.microservices.transaccion_service.entity.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    List<Transaccion> findByCuentaId(Long cuentaId);
    List<Transaccion> findTop5ByCuentaIdOrderByFechaDesc(Long cuentaId);

}
