package com.microservices.cuenta_service.client;

import com.microservices.cuenta_service.dto.TarjetaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import com.microservices.cuenta_service.dto.TarjetaDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "api-tarjetas")
public interface TarjetaClient {
    @PostMapping("/tarjetas/{cuentaId}")
    TarjetaResponse agregarTarjetaALaCuenta(@PathVariable Long cuentaId, @RequestBody TarjetaDTO tarjetaDTO);
    @GetMapping("/cuentas/{cuentaId}/tarjetas")
    List<TarjetaDTO> obtenerTarjetas(@PathVariable Long cuentaId);
    @GetMapping("/tarjetas/{cuentaId}")
    List<TarjetaResponse> obtenerTarjetasPorCuenta(@PathVariable Long cuentaId);
}
