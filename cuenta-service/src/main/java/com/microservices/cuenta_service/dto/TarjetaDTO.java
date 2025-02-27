package com.microservices.cuenta_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarjetaDTO {
        private String numeroTarjeta;
        private String nombreYApellido;
        private Long cuentaId;

    private Long id;
    private String fechaExpiracion;
}
