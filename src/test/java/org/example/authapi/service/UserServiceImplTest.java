package org.example.authapi.service;

import org.example.authapi.dto.UpdateUserRequest;
import org.example.authapi.dto.UserProfileResponse;
import org.example.authapi.model.Role;
import org.example.authapi.model.User;
import org.example.authapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserProfile_WhenUserExists_ShouldReturnProfile() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("john");
        user.setEmail("john@example.com");
        Role role = new Role();
        role.setName(Role.ERole.ROLE_USER);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        UserProfileResponse res = userService.getUserProfile("john");

        assertThat(res.getId()).isEqualTo(user.getId());
        assertThat(res.getUsername()).isEqualTo("john");
        assertThat(res.getEmail()).isEqualTo("john@example.com");
        assertThat(res.getRoles()).contains("ROLE_USER");
    }

    @Test
    void testGetUserProfile_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserProfile("john"));
    }

    @Test
    void testUpdateUser_WhenUsernameAndEmailUpdated_ShouldSaveUser() {
        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setRoles(new HashSet<>());

        UpdateUserRequest req = new UpdateUserRequest();
        req.setUsername("johnny");
        req.setEmail("johnny@example.com");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("johnny")).thenReturn(false);
        when(userRepository.existsByEmail("johnny@example.com")).thenReturn(false);

        UserProfileResponse res = userService.updateUser("john", req);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertThat(saved.getUsername()).isEqualTo("johnny");
        assertThat(saved.getEmail()).isEqualTo("johnny@example.com");

        assertThat(res.getUsername()).isEqualTo("johnny");
        assertThat(res.getEmail()).isEqualTo("johnny@example.com");
    }

    @Test
    void testUpdateUser_WhenUsernameExists_ShouldThrowException() {
        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");

        UpdateUserRequest req = new UpdateUserRequest();
        req.setUsername("existingUsername");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("existingUsername")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.updateUser("john", req));
    }

    @Test
    void testUpdateUser_WhenEmailExists_ShouldThrowException() {
        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");

        UpdateUserRequest req = new UpdateUserRequest();
        req.setEmail("existing@example.com");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.updateUser("john", req));
    }

    @Test
    void testUpdateUser_WhenUserNotFound_ShouldThrowException() {
        UpdateUserRequest req = new UpdateUserRequest();
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateUser("john", req));
    }
}
