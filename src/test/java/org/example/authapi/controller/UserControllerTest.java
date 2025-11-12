package org.example.authapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.authapi.dto.UpdateUserRequest;
import org.example.authapi.dto.UserProfileResponse;
import org.example.authapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    private UserProfileResponse userProfileResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");

        userProfileResponse = new UserProfileResponse(
                UUID.randomUUID(),
                "john",
                "john@example.com",
                roles
        );
    }

    @Test
    void testGetMe() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("john");

        when(userService.getUserProfile("john")).thenReturn(userProfileResponse);

        mockMvc.perform(get("/api/user/me").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));

        verify(userService, times(1)).getUserProfile("john");
    }

    @Test
    void testUpdateUser() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("john");

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setUsername("johnny");
        updateUserRequest.setEmail("johnny@example.com");

        UserProfileResponse updatedResponse = new UserProfileResponse(
                userProfileResponse.getId(),
                "johnny",
                "johnny@example.com",
                userProfileResponse.getRoles()
        );

        when(userService.updateUser(eq("john"), any(UpdateUserRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/user/update")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johnny"))
                .andExpect(jsonPath("$.email").value("johnny@example.com"));

        verify(userService, times(1)).updateUser(eq("john"), any(UpdateUserRequest.class));
    }
}
