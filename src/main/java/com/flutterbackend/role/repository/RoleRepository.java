package com.flutterbackend.role.repository;

import com.flutterbackend.role.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
