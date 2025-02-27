package com.microservices.cuenta_service.client;

import com.microservices.cuenta_service.dto.AuthResponse;
import com.microservices.cuenta_service.dto.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service")
public interface UsuarioClient {
    @GetMapping("/api/usuarios/{id}")
    UsuarioDTO obtenerUsuarioPorId(@PathVariable Long id);
    @PostMapping("/api/auth/login")
    AuthResponse login(@RequestBody AuthRequest authRequest);
}
