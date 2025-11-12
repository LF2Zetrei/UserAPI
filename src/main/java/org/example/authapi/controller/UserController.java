package org.example.authapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.authapi.dto.UpdateUserRequest;
import org.example.authapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication authentication) {
        return ResponseEntity.ok(
                userService.getUserProfile(authentication.getName())
        );
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserRequest request, Authentication authentication) {
        return ResponseEntity.ok(
                userService.updateUser(authentication.getName(), request)
        );
    }

}
