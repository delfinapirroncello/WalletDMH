package api_tarjetas.dto;

import java.math.BigDecimal;

public class CuentaDTO {
    private Long id;
    private String numeroCuenta;
    private BigDecimal saldoDisponible;
    private String nombreYApellido;
    private Long usuarioId;

    public CuentaDTO() {
    }

    public CuentaDTO(Long id, String numeroCuenta, BigDecimal saldoDisponible, String nombreYApellido, Long usuarioId) {
        this.id = id;
        this.numeroCuenta = numeroCuenta;
        this.saldoDisponible = saldoDisponible;
        this.nombreYApellido = nombreYApellido;
        this.usuarioId = usuarioId;
    }

    public String getNombreYApellido() {
        return nombreYApellido;
    }

    public void setNombreYApellido(String nombreYApellido) {
        this.nombreYApellido = nombreYApellido;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public BigDecimal getSaldoDisponible() {
        return saldoDisponible;
    }

    public void setSaldoDisponible(BigDecimal saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    @Override
    public String toString() {
        return "CuentaDTO{" +
                "id=" + id +
                ", numeroCuenta='" + numeroCuenta + '\'' +
                ", saldo=" + saldoDisponible +
                ", nombreYApellido='" + nombreYApellido + '\'' +
                ", usuarioId=" + usuarioId +
                '}';
    }
}
