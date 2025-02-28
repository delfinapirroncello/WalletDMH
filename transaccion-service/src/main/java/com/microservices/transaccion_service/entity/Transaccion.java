package com.microservices.transaccion_service.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.microservices.transaccion_service.enums.TipoTransaccion;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transaccionId;

    private Long cuentaId;
    private BigDecimal cantidad;
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private TipoTransaccion tipo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fecha;

    public Transaccion() {
        this.fecha = LocalDateTime.now();
    }

    public Transaccion(Long transaccionId, Long cuentaId, BigDecimal cantidad, String descripcion, TipoTransaccion tipo, LocalDateTime fecha) {
        this.transaccionId = transaccionId;
        this.cuentaId = cuentaId;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.tipo = tipo;
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

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransaccion tipo) {
        this.tipo = tipo;
    }
}
