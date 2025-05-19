package com.spring.ebankingbackend.security.repositories;

import com.spring.ebankingbackend.security.entities.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {
    Optional<AppRole> findByName(String name);
}
