package com.microservices.user_service.service.impl;

import com.microservices.user_service.service.AuthService;
import com.microservices.user_service.config.JwtService;
import com.microservices.user_service.entity.Usuario;
import com.microservices.user_service.config.JwtTokenBlacklist;
import com.microservices.user_service.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final JwtTokenBlacklist jwtTokenBlacklist;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    @Autowired
    public AuthServiceImpl(JwtTokenBlacklist jwtTokenBlacklist, JwtService jwtService, UsuarioService usuarioService) {
        this.jwtTokenBlacklist = jwtTokenBlacklist;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @Override
    public String login(String email, String contraseña) {
        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorEmail(email);

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        if (!usuario.getContraseña().equals(contraseña)) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return jwtService.generarToken(String.valueOf(usuario.getId()));
    }

    @Override
    public void logout(String token) {
        jwtTokenBlacklist.revokeToken(token);
    }

    @Override
    public boolean isTokenValid(String token) {
        if (jwtTokenBlacklist.isTokenRevoked(token)) {
            return false;
        }
        return jwtService.validarToken(token);
    }
}
