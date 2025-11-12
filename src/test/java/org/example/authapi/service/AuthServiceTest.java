package org.example.authapi.service;

import org.example.authapi.dto.*;
import org.example.authapi.model.RefreshToken;
import org.example.authapi.model.Role;
import org.example.authapi.model.User;
import org.example.authapi.repository.RefreshTokenRepository;
import org.example.authapi.repository.RoleRepository;
import org.example.authapi.repository.UserRepository;
import org.example.authapi.security.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JWTUtils jwtUtils;
    @Mock
    private JWTBlacklistService jwtBlacklistService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- TEST 1 : register() ---
    @Test
    void testRegister_WhenUserDoesNotExist_ShouldSaveUser() {
        RegisterRequest req = new RegisterRequest("john", "john@example.com", "password");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        Role role = new Role();
        role.setName(Role.ERole.ROLE_USER);
        when(roleRepository.findByName(Role.ERole.ROLE_USER)).thenReturn(Optional.of(role));

        MessageResponse res = authService.register(req);

        assertThat(res.getMessage()).isEqualTo("User registered successfully");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_WhenUsernameExists_ShouldReturnMessage() {
        RegisterRequest req = new RegisterRequest("john", "john@example.com", "password");
        when(userRepository.existsByUsername("john")).thenReturn(true);

        MessageResponse res = authService.register(req);

        assertThat(res.getMessage()).isEqualTo("Username already exists");
        verify(userRepository, never()).save(any(User.class));
    }

    // --- TEST 2 : login() ---
    @Test
    void testLogin_WhenValidCredentials_ShouldReturnJWTResponse() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("encoded");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        when(jwtUtils.generateJwtToken("john")).thenReturn("jwt-token");

        RefreshToken refresh = new RefreshToken();
        refresh.setToken("refresh-token");
        refresh.setExpriryDate(Instant.now().plusSeconds(3600));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refresh);

        LoginRequest req = new LoginRequest("john", "password");

        JWTResponse res = authService.login(req);

        assertThat(res.getAccessToken()).isEqualTo("jwt-token");
        assertThat(res.getRefreshToken()).isNotEmpty();
        assertThat(res.getUsername()).isEqualTo("john");
    }

    @Test
    void testLogin_WhenWrongPassword_ShouldThrowException() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("encoded");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(false);

        LoginRequest req = new LoginRequest("john", "password");
        assertThrows(RuntimeException.class, () -> authService.login(req));
    }

    // --- TEST 3 : refreshToken() ---
    @Test
    void testRefreshToken_WhenValid_ShouldReturnNewTokens() {
        User user = new User();
        user.setUsername("john");

        RefreshToken oldToken = new RefreshToken();
        oldToken.setUser(user);
        oldToken.setToken("old-token");
        oldToken.setExpriryDate(Instant.now().plusSeconds(3600));

        when(refreshTokenRepository.findByToken("old-token")).thenReturn(Optional.of(oldToken));
        when(jwtUtils.generateJwtToken("john")).thenReturn("new-jwt");

        JWTResponse res = authService.refreshToken(new RefreshTokenRequest("old-token"));

        assertThat(res.getAccessToken()).isEqualTo("new-jwt");
        assertThat(res.getUsername()).isEqualTo("john");
        assertThat(res.getRefreshToken()).isNotEmpty();

        verify(refreshTokenRepository, times(1)).delete(oldToken);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void testRefreshToken_WhenExpired_ShouldThrowException() {
        User user = new User();
        user.setUsername("john");
        RefreshToken expired = new RefreshToken();
        expired.setUser(user);
        expired.setExpriryDate(Instant.now().minusSeconds(10));

        when(refreshTokenRepository.findByToken("old-token")).thenReturn(Optional.of(expired));

        assertThrows(RuntimeException.class, () ->
                authService.refreshToken(new RefreshTokenRequest("old-token")));
        verify(refreshTokenRepository, times(1)).delete(expired);
    }

    // --- TEST 4 : logout() ---
    @Test
    void testLogout_ShouldBlacklistTokenAndDeleteRefreshTokens() {
        String jwt = "some-jwt";
        LogoutRequest req = new LogoutRequest(jwt);
        User user = new User();
        user.setUsername("john");

        when(jwtUtils.getUsernameFromJwtToken(jwt)).thenReturn("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        MessageResponse res = authService.logout(req);

        assertThat(res.getMessage()).isEqualTo("User logged out successfully");
        verify(jwtBlacklistService, times(1)).blacklistToken(jwt);
        verify(refreshTokenRepository, times(1)).deleteByUser(user);
    }
}
