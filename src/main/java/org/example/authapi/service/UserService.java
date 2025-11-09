package org.example.authapi.service;

import org.example.authapi.dto.UpdateUserRequest;
import org.example.authapi.dto.UserProfileResponse;

public interface UserService {
    UserProfileResponse getUserProfile(String username);
    UserProfileResponse updateUser(String username, UpdateUserRequest request);
}
