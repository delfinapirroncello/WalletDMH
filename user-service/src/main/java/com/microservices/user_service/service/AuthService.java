package com.microservices.user_service.service;

public interface AuthService {
    String login(String email, String contraseña);

    void logout(String token);

    boolean isTokenValid(String token);
}