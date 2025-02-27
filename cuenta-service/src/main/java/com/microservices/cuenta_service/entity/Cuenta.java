package com.microservices.cuenta_service.entity;

import com.microservices.cuenta_service.dto.TarjetaResponse;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@Entity
@Table(name = "cuenta", schema = "usuarios")
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cvu;
    private String alias;
    private String tipoCuenta;
    private String numeroCuenta;
    @Column(name = "nombreYApellido")
    private String nombreYApellido;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "saldoDisponible")
    private BigDecimal saldoDisponible;

    @Column(nullable = false)
    private Long usuarioId;
    @ElementCollection
    private List<Long> transaccionIds = new ArrayList<>();
    @Transient
    private List<TarjetaResponse> tarjetas;


    public Cuenta() {}

    public Cuenta(Long id, String nombreYApellido, String cvu, String alias, BigDecimal saldoDisponible, String tipoCuenta, String numeroCuenta, String telefono, Long usuarioId, List<Long> transaccionIds, List<TarjetaResponse> tarjetas) {
        this.id = id;
        this.nombreYApellido = nombreYApellido;
        this.cvu = cvu;
        this.alias = alias;
        this.saldoDisponible = saldoDisponible;
        this.tipoCuenta = tipoCuenta;
        this.numeroCuenta = numeroCuenta;
        this.telefono = telefono;
        this.usuarioId = usuarioId;
        this.transaccionIds = transaccionIds;
        this.tarjetas = tarjetas;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCvu() {
        return cvu;
    }

    public void setCvu(String cvu) {
        this.cvu = cvu;
    }

    public BigDecimal getSaldoDisponible() {
        return saldoDisponible;
    }

    public void setSaldoDisponible(BigDecimal saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
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

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<Long> getTransaccionIds() {
        return transaccionIds;
    }

    public void setTransaccionIds(List<Long> transaccionIds) {
        this.transaccionIds = transaccionIds;
    }

    public void agregarTransaccion(Long transaccionId) {
        this.transaccionIds.add(transaccionId); // Agrega el ID de la transacción
    }

    public void eliminarTransaccion(Long transaccionId) {
        this.transaccionIds.remove(transaccionId); // Elimina el ID de la transacción
    }

    public String getNombreYApellido() {
        return nombreYApellido;
    }

    public void setNombreYApellido(String nombreYApellido) {
        this.nombreYApellido = nombreYApellido;
    }

    public List<TarjetaResponse> getTarjetas() {
        return tarjetas;
    }

    public void setTarjetas(List<TarjetaResponse> tarjetas) {
        this.tarjetas = tarjetas;
    }
}
