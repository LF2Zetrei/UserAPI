package org.example.authapi.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.authapi.model.Role;
import org.example.authapi.repository.RoleRepository;
import org.example.authapi.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;

    @PostConstruct
    public void initRoles() {
        if (roleRepository.findByName(Role.ERole.ROLE_USER).isEmpty()){
            roleRepository.save(new Role(null, Role.ERole.ROLE_USER));
        }
        if (roleRepository.findByName(Role.ERole.ROLE_ADMIN).isEmpty()){
            roleRepository.save(new Role(null, Role.ERole.ROLE_ADMIN));
        }
    }
}

