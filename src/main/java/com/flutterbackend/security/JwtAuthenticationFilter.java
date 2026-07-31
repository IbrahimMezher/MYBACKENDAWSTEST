package com.flutterbackend.security;

import com.flutterbackend.user.repository.UserRepository;
import com.flutterbackend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        String email;
        try {
            email = jwtUtil.extractEmail(token);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails user = userRepository.findByEmail(email).orElse(null);

            String path = request.getRequestURI();
            boolean isVerificationPath = path != null && (
                    path.endsWith("/auth/get_otp")
                    || path.endsWith("/auth/verify_otp")
                    || path.endsWith("/auth/send-phone-otp")
                    || path.endsWith("/auth/verify-phone-otp")
                    || path.endsWith("/auth/status")
                    || path.endsWith("/auth/change-email"));

            boolean okStatus;
            if (user instanceof com.flutterbackend.user.domain.User) {
                com.flutterbackend.user.domain.User u =
                        (com.flutterbackend.user.domain.User) user;
                boolean active = u.getStatus()
                        == com.flutterbackend.user.domain.UserStatus.ACTIVE;
                boolean pending = u.getStatus()
                        == com.flutterbackend.user.domain.UserStatus.PENDING;
                okStatus = active || (pending && isVerificationPath);
            } else {
                okStatus = user != null && user.isEnabled();
            }

            if (user != null && okStatus
                    && jwtUtil.validateToken(token, user.getUsername())) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities()
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
