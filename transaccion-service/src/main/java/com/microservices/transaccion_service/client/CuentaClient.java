package com.microservices.transaccion_service.client;

import com.microservices.transaccion_service.dto.CuentaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "cuenta-service")
public interface CuentaClient {
    @GetMapping("/api/cuentas/{cuentaId}")
    CuentaDTO obtenerCuenta(
            @PathVariable("cuentaId") Long cuentaId,
            @RequestHeader("Authorization") String token
    );
    @PutMapping("/api/cuentas/{cuentaId}")
    void actualizarCuenta(
            @PathVariable Long cuentaId,
            @RequestBody CuentaDTO cuenta,
            @RequestHeader("Authorization") String token
    );

    @GetMapping("/api/cuentas")
    default List<CuentaDTO> obtenerTodasLasCuentas(@RequestHeader("Authorization") String token) {
        return null;
    }

    @GetMapping("/api/cuentas/identificador/{identificador}")
    CuentaDTO obtenerCuentaPorIdentificador(
            @PathVariable("identificador") String identificador,
            @RequestHeader("Authorization") String token
    );
}
