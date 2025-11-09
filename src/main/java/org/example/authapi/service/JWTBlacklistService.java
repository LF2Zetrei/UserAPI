package org.example.authapi.service;

import org.example.authapi.model.JwtBlacklist;
import org.example.authapi.repository.JWTBlacklistRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class JWTBlacklistService {
    private final JWTBlacklistRepository jwtBlacklistRepository;

    public JWTBlacklistService(JWTBlacklistRepository jwtBlacklistRepository) {
        this.jwtBlacklistRepository = jwtBlacklistRepository;
    }

    public void blacklistToken(String token) {
        if(!isBlacklisted(token)) {
            JwtBlacklist jwt = new JwtBlacklist();
            jwt.setToken(token);
            jwt.setBlacklistedAt(Instant.now());
            jwtBlacklistRepository.save(jwt);
        }
    }

    public boolean isBlacklisted(String token) {
        return jwtBlacklistRepository.existsByToken(token);
    }
}
