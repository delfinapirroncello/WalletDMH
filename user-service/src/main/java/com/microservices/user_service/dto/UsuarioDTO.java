package com.microservices.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class UsuarioDTO {
    private Long id;
    private String nombreYApellido;
    private String email;
    private String telefono;
    private String alias;
    private String cvu;
    private Long cuentaId;

    public UsuarioDTO(Long id, String nombreYApellido, String email, String telefono, String alias, String cvu) {
        this.id = id;
        this.nombreYApellido = nombreYApellido;
        this.email = email;
        this.telefono = telefono;
        this.alias = alias;
        this.cvu = cvu;
        this.cuentaId = null;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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
    public Long getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(Long cuentaId) {
        this.cuentaId = cuentaId;
    }
}