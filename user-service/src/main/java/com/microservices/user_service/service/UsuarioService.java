package com.microservices.user_service.service;

import com.microservices.user_service.dto.TransaccionDTO;
import com.microservices.user_service.entity.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

    public interface UsuarioService {
        Usuario crearUsuario(Usuario usuario);
        Optional<Usuario> obtenerUsuarioPorId(Long id);
        Usuario actualizarUsuario(Long id, Usuario usuario);
        Optional<Usuario> validarUsuario(String email, String contraseña);
        List<Usuario> obtenerTodosLosUsuarios();
        void eliminarUsuario(Long id);
        public boolean existeEmailOTelefono(String email, String telefono);

        public Optional<Usuario> obtenerUsuarioPorEmail(String email);

        public List<TransaccionDTO> obtenerActividadUsuario(Long userId);

    }
