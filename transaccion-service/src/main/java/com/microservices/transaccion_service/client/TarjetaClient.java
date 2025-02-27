package com.microservices.transaccion_service.client;

import com.microservices.transaccion_service.dto.TarjetaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "api-tarjetas")
public interface TarjetaClient {
    @GetMapping("/api/tarjetas/tarjetas")
    List<TarjetaDTO> obtenerTarjetas(@RequestHeader("Authorization") String token);

}