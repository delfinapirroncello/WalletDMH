package api_tarjeta.dto;

import api_tarjeta.entity.Tarjetas;
@Deprecated
public class TarjetaRequest {
    private String nombreYApellido;
    private String fechaExpiracion;
    private String cvv;

    public TarjetaRequest() {
    }

    public TarjetaRequest( String nombreYApellido, String fechaExpiracion, String cvv) {
        this.nombreYApellido = nombreYApellido;
        this.fechaExpiracion = fechaExpiracion;
        this.cvv = cvv;
    }

    public String getNombreYApellido() { return nombreYApellido; }
    public void setNombreYApellido(String nombreYApellido) { this.nombreYApellido = nombreYApellido; }

    public String getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(String fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
}
