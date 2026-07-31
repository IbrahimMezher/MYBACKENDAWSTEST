package com.flutterbackend.user.repository;

import com.flutterbackend.user.domain.User;
import com.flutterbackend.user.domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRoleNameAndStatus(String roleName, UserStatus status);
    Optional<User> findByResetToken(String token);
    Optional<User> findByPhoneNumber(String phoneNumber);
}
