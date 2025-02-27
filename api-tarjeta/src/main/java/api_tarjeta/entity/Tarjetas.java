package api_tarjeta.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tarjetas")
public class Tarjetas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroTarjeta;

    @Column(nullable = false)
    private String nombreYApellido;

    @Column(nullable = false)
    private String tipoTarjeta;

    @Column(nullable = false)
    private String fechaExpiracion;

    @Column(nullable = false)
    private String cvv;

    @Column(nullable = false)
    private Long cuentaId;

    public Tarjetas() {}

    public Tarjetas(String numeroTarjeta, String nombreYApellido, String tipoTarjeta, String fechaExpiracion, String cvv, Long cuentaId) {
        this.numeroTarjeta = numeroTarjeta;
        this.nombreYApellido = nombreYApellido;
        this.tipoTarjeta = tipoTarjeta;
        this.fechaExpiracion = fechaExpiracion;
        this.cvv = cvv;
        this.cuentaId = cuentaId;
    }

    public Long getId() {
        return id;
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

    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    public String getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(String fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(Long cuentaId) {
        this.cuentaId = cuentaId;
    }
}
