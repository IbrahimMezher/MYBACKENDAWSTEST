package com.flutterbackend.admin.repository;

import com.flutterbackend.user.domain.User;
import com.flutterbackend.user.domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndRole_Name(String email, String roleName);
    boolean existsByEmailAndRole_Name(String email, String roleName);
    List<User> findAllByRole_Name(String roleName);

    List<User> findByRoleNameAndStatus(String broker, UserStatus userStatus);
}
