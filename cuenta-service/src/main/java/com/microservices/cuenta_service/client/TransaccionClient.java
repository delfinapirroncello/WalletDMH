package com.microservices.cuenta_service.client;

import com.microservices.cuenta_service.dto.TransaccionDTO;
import com.microservices.cuenta_service.dto.TransaccionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "transaccion-service")
public interface TransaccionClient {
    @GetMapping("/api/transacciones/cuenta/{id}/transacciones")
    List<TransaccionDTO> obtenerTransaccionesPorCuenta(@PathVariable Long id);
}