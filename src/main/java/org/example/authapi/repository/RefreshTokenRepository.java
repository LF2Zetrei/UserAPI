package org.example.authapi.repository;

import org.example.authapi.model.RefreshToken;
import org.example.authapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    void deletedByUser(User user);
}
