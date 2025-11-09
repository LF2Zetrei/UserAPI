package org.example.authapi.service;

import org.example.authapi.dto.*;
import org.example.authapi.model.RefreshToken;
import org.example.authapi.model.User;
import org.example.authapi.repository.RefreshTokenRepository;
import org.example.authapi.repository.UserRepository;
import org.example.authapi.security.JWTUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTBlacklistService jwtBlacklistService;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       JWTBlacklistService jwtBlacklistService,
                       PasswordEncoder passwordEncoder,
                       JWTUtils jwtUtils) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtBlacklistService = jwtBlacklistService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public MessageResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return new MessageResponse("Username already exists");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return new MessageResponse("Email already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.save(user);

        return new MessageResponse("User registered successfully");
    }

    public JWTResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Username not found"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        String jwt = jwtUtils.generateJwtToken(user.getUsername());
        RefreshToken refreshToken = createRefreshToken(user);

        return new JWTResponse(jwt, user.getUsername());
    }

    public JWTResponse refreshToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refreshToken.getExpriryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        String newJwt = jwtUtils.generateJwtToken(refreshToken.getUser().getUsername());
        return new JWTResponse(newJwt, refreshToken.getUser().getUsername());
    }

    public MessageResponse logout(LogoutRequest request) {
        String jwt = request.getJwt();

        jwtBlacklistService.blacklistToken(jwt);

        String username = jwtUtils.getUsernameFromJwtToken(jwt);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username not found"));

        refreshTokenRepository.deleteByUser(user);

        return new MessageResponse("User logged out successfully");
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpriryDate(Instant.now().plusSeconds(refreshExpiration));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }
}
