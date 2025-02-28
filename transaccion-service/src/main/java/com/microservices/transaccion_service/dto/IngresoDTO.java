package com.microservices.transaccion_service.dto;

import java.math.BigDecimal;

public class IngresoDTO {
    private Long tarjetaId;
    private BigDecimal cantidad;
    public Long getTarjetaId() {
        return tarjetaId;
    }
    public void setTarjetaId(Long tarjetaId) {
        this.tarjetaId = tarjetaId;
    }
    public BigDecimal getCantidad() {
        return cantidad;
    }
    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }
}