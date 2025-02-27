package com.microservices.user_service.entity;

public class AuthToken {
    private String token;
    private Long userId;

    // Constructor
    public AuthToken(String token, Long userId) {
        this.token = token;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }
}