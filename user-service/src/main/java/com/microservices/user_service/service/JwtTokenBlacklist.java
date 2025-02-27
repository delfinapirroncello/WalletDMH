package com.microservices.user_service.service;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class JwtTokenBlacklist {

    private final Set<String> revokedTokens = new HashSet<>();

    public void revokeToken(String token) {
        revokedTokens.add(token);
    }

    public boolean isTokenRevoked(String token) {
        return revokedTokens.contains(token);
    }
}
