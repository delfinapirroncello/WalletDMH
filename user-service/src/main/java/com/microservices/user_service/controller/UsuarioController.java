package com.microservices.user_service.controller;

import com.microservices.user_service.dto.ApiResponse;
import com.microservices.user_service.dto.TransaccionDTO;
import com.microservices.user_service.dto.UsuarioDTO;
import com.microservices.user_service.entity.Usuario;
import com.microservices.user_service.service.AliasCvuService;
import com.microservices.user_service.service.AuthService;
import com.microservices.user_service.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private AuthService authService;
    private final AliasCvuService aliasCvuService;


    public UsuarioController(UsuarioService usuarioService, AliasCvuService aliasCvuService) {
        this.usuarioService = usuarioService;
        this.aliasCvuService = aliasCvuService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> crearUsuario(@RequestBody Usuario usuario) {
        try {
            if (usuario.getNombreYApellido() == null || usuario.getNombreYApellido().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse("El campo nombre y apellido no puede estar vacío.", false, null));
            }

            if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse("El campo email no puede estar vacío.", false, null));
            }

            if (usuario.getContraseña() == null || usuario.getContraseña().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse("El campo contraseña no puede estar vacío.", false, null));
            }

            if (usuarioService.existeEmailOTelefono(usuario.getEmail(), usuario.getTelefono())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse("El correo o el teléfono ya están en uso", false, null));
            }

                usuario.setAlias(aliasCvuService.generarAlias());
            usuario.setCvu(aliasCvuService.generarCvu());

            Usuario usuarioGuardado = usuarioService.crearUsuario(usuario);

            UsuarioDTO usuarioDTO = new UsuarioDTO(
                    usuarioGuardado.getId(),
                    usuarioGuardado.getNombreYApellido(),
                    usuarioGuardado.getEmail(),
                    usuarioGuardado.getTelefono(),
                    usuarioGuardado.getAlias(),
                    usuarioGuardado.getCvu()

            );

            return ResponseEntity.ok(new ApiResponse("Usuario creado exitosamente", true, usuarioDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al crear el usuario: " + e.getMessage(), false, null));
        }
    }

    @GetMapping("/{id}")
    public UsuarioDTO obtenerUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorId(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            return new UsuarioDTO(
                    usuario.getId(),
                    usuario.getNombreYApellido(),
                    usuario.getEmail(),
                    usuario.getTelefono(),
                    usuario.getAlias(),
                    usuario.getCvu()
            );
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
    }



    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, usuario);
            UsuarioDTO usuarioDTO = new UsuarioDTO(
                    usuarioActualizado.getId(),
                    usuarioActualizado.getNombreYApellido(),
                    usuarioActualizado.getEmail(),
                    usuarioActualizado.getTelefono(),
                    usuarioActualizado.getAlias(),
                    usuarioActualizado.getCvu()
            );
            return ResponseEntity.ok(new ApiResponse("Usuario actualizado exitosamente", true, usuarioDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Usuario no encontrado: " + e.getMessage(), false, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al actualizar el usuario: " + e.getMessage(), false, null));
        }
    }


    @GetMapping
    public ResponseEntity<ApiResponse> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();

        List<UsuarioDTO> usuariosDTO = usuarios.stream()
                .map(usuario -> new UsuarioDTO(
                        usuario.getId(),
                        usuario.getNombreYApellido(),
                        usuario.getEmail(),
                        usuario.getTelefono(),
                        usuario.getAlias(),
                        usuario.getCvu()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse("Lista de usuarios", true, usuariosDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok(new ApiResponse("Usuario eliminado exitosamente", true, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Usuario no encontrado: " + e.getMessage(), false, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al eliminar el usuario: " + e.getMessage(), false, null));
        }
    }

    @GetMapping("/{id}/actividad")
    public ResponseEntity<?> obtenerActividadUsuario(@PathVariable Long id) {
        try {
            List<TransaccionDTO> transacciones = usuarioService.obtenerActividadUsuario(id);
            if (transacciones.isEmpty()) {
                return ResponseEntity.ok("El usuario no tiene actividad registrada.");
            }
            return ResponseEntity.ok(transacciones);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener la actividad del usuario: " + e.getMessage());
        }
    }


}
