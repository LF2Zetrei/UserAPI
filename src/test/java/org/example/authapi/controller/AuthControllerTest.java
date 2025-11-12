package org.example.authapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.authapi.dto.*;
import org.example.authapi.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AuthController.class)
@Import(AuthController.class) // pour s'assurer que le controller est injecté avec le mock
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private LogoutRequest logoutRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("john");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("password");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("john");
        loginRequest.setPassword("password");

        refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken("refresh-token");

        logoutRequest = new LogoutRequest();
        logoutRequest.setJwt("jwt-token");
    }

    @Test
    void testRegister() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(new MessageResponse("User registered successfully"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));

        verify(authService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void testLogin() throws Exception {
        JWTResponse jwtResponse = new JWTResponse("jwt", "john", "refresh-token");
        when(authService.login(any(LoginRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("jwt"))
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void testRefresh() throws Exception {
        JWTResponse jwtResponse = new JWTResponse("new-jwt", "john", "new-refresh-token");
        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("new-jwt"))
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));

        verify(authService, times(1)).refreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    void testLogout() throws Exception {
        doNothing().when(authService).logout(any(LogoutRequest.class));

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("logged out successfully"));

        verify(authService, times(1)).logout(any(LogoutRequest.class));
    }
}
