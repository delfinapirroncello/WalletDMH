package com.microservices.cuenta_service.dto;

import java.math.BigDecimal;

public class CuentaResponse {
    private Long id;
    private String nombreYApellido;
    private String numeroCuenta;
    private String alias;
    private String cvu;
    private String telefono;
    private BigDecimal saldoDisponible;

    public CuentaResponse(Long id, String nombreYApellido, String numeroCuenta,
                          String alias, String cvu, String telefono, BigDecimal saldoDisponible) {
        this.id = id;
        this.nombreYApellido = nombreYApellido;
        this.numeroCuenta = numeroCuenta;
        this.alias = alias;
        this.cvu = cvu;
        this.telefono = telefono;
        this.saldoDisponible = saldoDisponible;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreYApellido() {
        return nombreYApellido;
    }

    public void setNombreYApellido(String nombreYApellido) {
        this.nombreYApellido = nombreYApellido;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCvu() {
        return cvu;
    }

    public void setCvu(String cvu) {
        this.cvu = cvu;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public BigDecimal getSaldoDisponible() {
        return saldoDisponible;
    }

    public void setSaldoDisponible(BigDecimal saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
    }
}
