package com.microservices.user_service.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.user_service.client.TarjetaClient;
import com.microservices.user_service.client.TransaccionClient;
import com.microservices.user_service.dto.TransaccionDTO;
import com.microservices.user_service.entity.Usuario;
import com.microservices.user_service.repository.UsuarioRepository;
import com.microservices.user_service.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    private static final String JSON_FILE_PATH = "usuarios.json";
    private final Map<Long, Usuario> usuarios = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private TransaccionClient transaccionClient;

    public UsuarioServiceImpl() {
        cargarUsuariosJson();
    }

    @Autowired
    private TarjetaClient tarjetaClient;

    @Override
    public List<Usuario> obtenerTodosLosUsuarios() {
        return (List<Usuario>) usuarioRepository.findAll();
    }

    @Override
    public Usuario crearUsuario(Usuario usuario) {
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        guardarUsuariosJson();
        return usuarioGuardado;
    }

    public boolean existeEmailOTelefono(String email, String telefono) {
        return usuarioRepository.existsByEmail(email) || usuarioRepository.existsByTelefono(telefono);
    }
    @Override
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }


    @Override
    public Usuario actualizarUsuario(Long id, Usuario usuario) {
        return usuarioRepository.findById(id).map(u -> {
            u.setNombreYApellido(usuario.getNombreYApellido());
            u.setEmail(usuario.getEmail());
            u.setTelefono(usuario.getTelefono());
            u.setContraseña(usuario.getContraseña());
            Usuario usuarioActualizado = usuarioRepository.save(u);
            guardarUsuariosJson();
            return usuarioActualizado;
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }


    public Optional<Usuario> validarUsuario(String email, String contraseña) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getContraseña().equals(contraseña)) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }


    private void cargarUsuariosJson() {
        File file = new File(JSON_FILE_PATH);
        if (file.exists()) {
            try {
                List<Usuario> usuariosList = objectMapper.readValue(file, new TypeReference<List<Usuario>>() {});
                usuarios.clear();
                for (Usuario usuario : usuariosList) {
                    usuarios.put(usuario.getId(), usuario);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error al leer el archivo JSON: " + e.getMessage());
            }
        }
    }

    private void guardarUsuariosJson() {
        try {
            List<Usuario> usuariosList = usuarioRepository.findAll();
            objectMapper.writeValue(new File(JSON_FILE_PATH), usuariosList);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo JSON: " + e.getMessage());
        }
    }


    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("El usuario con ID " + id + " no existe.");
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /*public List<TransaccionDTO> obtenerActividadUsuario(Long userId) {
        return transaccionClient.getTransaccionesPorUsuario(userId);
    }*/

}

