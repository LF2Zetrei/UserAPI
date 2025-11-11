package org.example.authapi.service;

import jakarta.transaction.Transactional;
import org.example.authapi.dto.*;
import org.example.authapi.model.RefreshToken;
import org.example.authapi.model.Role;
import org.example.authapi.model.User;
import org.example.authapi.repository.RefreshTokenRepository;
import org.example.authapi.repository.RoleRepository;
import org.example.authapi.repository.UserRepository;
import org.example.authapi.security.JWTUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService {
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JWTUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTBlacklistService jwtBlacklistService;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       JWTBlacklistService jwtBlacklistService,
                       PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository,
                       JWTUtils jwtUtils) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtBlacklistService = jwtBlacklistService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
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
        Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                .orElseThrow(() -> new UsernameNotFoundException("Role not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

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

        return new JWTResponse(jwt, user.getUsername(), refreshToken.getToken());
    }

    public JWTResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        RefreshToken oldToken = refreshTokenRepository.findByToken(refreshTokenRequest.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (oldToken.getExpriryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(oldToken);
            throw new RuntimeException("Refresh token expired");
        }

        // supprimer l'ancien et créer un nouveau refresh token
        refreshTokenRepository.delete(oldToken);
        RefreshToken newRefreshToken = createRefreshToken(oldToken.getUser());

        String newJwt = jwtUtils.generateJwtToken(oldToken.getUser().getUsername());

        return new JWTResponse(newJwt, oldToken.getUser().getUsername(), newRefreshToken.getToken());
    }

    @Transactional
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
