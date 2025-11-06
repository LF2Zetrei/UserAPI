package org.example.authapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "jwt_blacklist")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtBlacklist {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(nullable = false)
    private Instant blacklistedAt;
}
