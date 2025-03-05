package com.microservices.user_service.controller;

import com.microservices.user_service.dto.ApiResponse;
import com.microservices.user_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String contraseña = credenciales.get("contraseña");

        if (email == null || contraseña == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Email y contraseña son obligatorios.", false, null));
        }

        try {
            String token = authService.login(email, contraseña);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);

            return ResponseEntity.ok(new ApiResponse("Login exitoso", true, response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(e.getMessage(), false, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error interno: " + e.getMessage(), false, null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");

        if (!authService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Token inválido o ya expirado.", false, null));
        }

        authService.logout(token);
        return ResponseEntity.ok(new ApiResponse("Sesión cerrada correctamente.", true, null));
    }
}