package com.microservices.user_service.client;

import com.microservices.user_service.dto.TransaccionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "transaccion-service")
public interface TransaccionClient {

    @GetMapping("/api/transacciones/usuario/{userId}")
    List<TransaccionDTO> getTransaccionesPorUsuario(@PathVariable("userId") Long userId);
}
