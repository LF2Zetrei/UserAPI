package org.example.authapi.repository;

import org.example.authapi.model.JwtBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JWTBlacklistRepository extends JpaRepository<JwtBlacklist, UUID> {
    Optional<JwtBlacklist> findByToken(String token);
    boolean existsByToken(String token);
}
