package com.microservices.transaccion_service.service;
import com.microservices.transaccion_service.dto.TransaccionDTO;
import com.microservices.transaccion_service.entity.Transaccion;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public interface TransaccionService {

    ResponseEntity<?> crearTransaccion(Long cuentaId, TransaccionDTO transaccionDTO, String token);

    List<Transaccion> getAllTransacciones();

    List<Transaccion> findByCuentaId(Long cuentaId);

    Optional<Transaccion> getTransaccionById(Long id);

    List<TransaccionDTO> obtenerTransaccionesPorCuenta(Long cuentaId);

    void eliminarTransaccion(Long id);

    Transaccion registrarTransaccion(Transaccion transaccion);
}