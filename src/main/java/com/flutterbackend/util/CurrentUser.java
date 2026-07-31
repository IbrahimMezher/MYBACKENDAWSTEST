package com.flutterbackend.util;

import com.flutterbackend.user.domain.User;
import com.flutterbackend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class CurrentUser {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public CurrentUser(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    public User getCurrentUser(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("No token found");
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void requireAdmin(User user) {
        String role = user.getRole().getName().toUpperCase();
        if (!role.equals("ADMIN") && !role.equals("SUPERADMIN")) {
            throw new RuntimeException("Admin privileges required");
        }
    }
}
