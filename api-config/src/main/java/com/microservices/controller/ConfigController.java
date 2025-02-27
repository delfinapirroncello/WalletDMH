package com.microservices.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @GetMapping("/tarjeta.limite")
    public Double getLimiteMaximo() {
        return 50000.0;
    }

    @GetMapping("/tarjeta.cantidad.maxima")
    public Double getCantidadMaxima() {
        return 5.0;
    }

    @PostMapping("/config")
    public Map<String, String> getConfig(@RequestBody Map<String, String> configuraciones) {
        // Se pueden recibir configuraciones dinámicas y devolverlas
        return configuraciones;  // Devuelve las configuraciones recibidas para verlas en la respuesta
    }
}
