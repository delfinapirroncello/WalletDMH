package com.microservices.transaccion_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class TarjetaDTO {
    private String numeroTarjeta;
    private String nombreYApellido;
    private Long cuentaId;
    private Long id;
    private String fechaExpiracion;

    public TarjetaDTO() {
    }

    public TarjetaDTO(String numeroTarjeta, String nombreYApellido, Long cuentaId, Long id, String fechaExpiracion) {
        this.numeroTarjeta = numeroTarjeta;
        this.nombreYApellido = nombreYApellido;
        this.cuentaId = cuentaId;
        this.id = id;
        this.fechaExpiracion = fechaExpiracion;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getNombreYApellido() {
        return nombreYApellido;
    }

    public void setNombreYApellido(String nombreYApellido) {
        this.nombreYApellido = nombreYApellido;
    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(Long cuentaId) {
        this.cuentaId = cuentaId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(String fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }
}