package api_tarjetas.dto;
import jakarta.validation.constraints.NotNull;


public class TarjetaDTO {
    private String numeroTarjeta;
    @NotNull(message = "El nombre y apellido Mno puede ser nulo")
    private String nombreYApellido;
    private String tipoTarjeta;
    private String fechaExpiracion;
    private String cvv;
    private Long cuentaId;

    public TarjetaDTO() {
    }

    public TarjetaDTO(String numeroTarjeta, String nombreYApellido, String tipoTarjeta, String fechaExpiracion, String cvv, Long cuentaId) {
        this.numeroTarjeta = numeroTarjeta;
        this.nombreYApellido = nombreYApellido;
        this.tipoTarjeta = tipoTarjeta;
        this.fechaExpiracion = fechaExpiracion;
        this.cvv = cvv;
        this.cuentaId = cuentaId;
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
