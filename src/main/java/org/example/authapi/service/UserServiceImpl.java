package org.example.authapi.service;

import lombok.RequiredArgsConstructor;
import org.example.authapi.dto.UpdateUserRequest;
import org.example.authapi.dto.UserProfileResponse;
import org.example.authapi.model.User;
import org.example.authapi.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserProfileResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    @Override
    public UserProfileResponse updateUser(String currentUsername, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getUsername().equals(updateUserRequest.getUsername()) && userRepository.existsByUsername(updateUserRequest.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (!user.getEmail().equals(updateUserRequest.getEmail()) && userRepository.existsByEmail(updateUserRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        user.setUsername(updateUserRequest.getUsername());
        user.setEmail(updateUserRequest.getEmail());

        userRepository.save(user);
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
