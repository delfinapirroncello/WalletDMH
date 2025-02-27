package com.microservices.transaccion_service.dto;

import com.microservices.transaccion_service.TipoTransaccion;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransaccionDTO {
    private Long transaccionId;
    private Long cuentaId;
    private BigDecimal cantidad;
    private LocalDateTime fecha;
    private String descripcion;
    @Enumerated(EnumType.STRING)
    private TipoTransaccion tipo;

    public TransaccionDTO(Long transaccionId, Long cuentaId, BigDecimal cantidad, String descripcion, TipoTransaccion tipo, LocalDateTime fecha) {
        this.transaccionId = transaccionId;
        this.cuentaId = cuentaId;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.fecha = fecha;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Long getTransaccionId() {
        return transaccionId;
    }

    public void setTransaccionId(Long transaccionId) {
        this.transaccionId = transaccionId;
    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(Long cuentaId) {
        this.cuentaId = cuentaId;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }
    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransaccion tipo) {
        this.tipo = tipo;
    }
}