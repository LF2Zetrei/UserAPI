package org.example.authapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name="roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private ERole name;

    public enum ERole {
        ROLE_USER,
        ROLE_ADMIN,
    }
}
