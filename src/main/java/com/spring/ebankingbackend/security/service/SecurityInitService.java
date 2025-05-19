package com.spring.ebankingbackend.security.service;

import com.spring.ebankingbackend.security.entities.AppRole;
import com.spring.ebankingbackend.security.entities.AppUser;
import com.spring.ebankingbackend.security.repositories.AppRoleRepository;
import com.spring.ebankingbackend.security.repositories.AppUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityInitService implements CommandLineRunner {

    private final AppRoleRepository roleRepository;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            log.info("Initializing security roles and admin user");
            initRoles();
            initAdminUser();
        }
    }

    private void initRoles() {
        // Create roles
        AppRole adminRole = AppRole.builder().name("ROLE_ADMIN").build();
        AppRole userRole = AppRole.builder().name("ROLE_USER").build();
        
        // Save roles
        roleRepository.save(adminRole);
        roleRepository.save(userRole);
        
        log.info("Created roles: ROLE_ADMIN, ROLE_USER");
    }

    private void initAdminUser() {
        // Get roles
        AppRole adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));
        AppRole userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("User role not found"));
        
        // Create admin user with both roles
        Collection<AppRole> adminRoles = new ArrayList<>();
        adminRoles.add(adminRole);
        adminRoles.add(userRole);
        
        AppUser adminUser = AppUser.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .email("admin@ebanking.com")
                .roles(adminRoles)
                .build();
        
        userRepository.save(adminUser);
        
        log.info("Created admin user with username: admin");
    }
}
