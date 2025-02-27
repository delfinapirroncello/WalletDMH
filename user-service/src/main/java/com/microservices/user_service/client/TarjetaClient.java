package com.microservices.user_service.client;

import com.microservices.user_service.dto.TarjetaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "api-tarjeta", url = "http://localhost:8083/api/tarjetas")
public interface TarjetaClient {
    @GetMapping("/{userId}")
    List<TarjetaDTO> getCardsByUserId(@PathVariable("userId") Long userId);
}
