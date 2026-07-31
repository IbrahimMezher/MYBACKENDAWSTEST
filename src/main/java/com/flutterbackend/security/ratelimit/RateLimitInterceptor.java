package com.flutterbackend.security.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;

    public RateLimitInterceptor(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {

        if (!"POST".equalsIgnoreCase(request.getMethod())) return true;
        String path = request.getRequestURI();
        String bucket = bucketFor(path);
        if (bucket == null) return true;

        rateLimitService.checkIp(clientIp(request), bucket);
        return true;
    }

    private String bucketFor(String path) {
        if (path == null) return null;
        if (path.endsWith("/users/login")) return "login";
        if (path.endsWith("/admin/auth/login")) return "login";
        if (path.endsWith("/users/signup")) return "signup";
        if (path.endsWith("/brokers/signup")) return "signup";
        if (path.endsWith("/auth/get_otp") || path.endsWith("/auth/verify_otp"))
            return "verify-email";
        if (path.endsWith("/auth/forgot-password")
                || path.endsWith("/auth/reset-password")) return "forgot-password";
        if (path.contains("/auth/2fa/") || path.contains("/admin/auth/2fa/")) return "2fa";
        if (path.endsWith("/auth/send-phone-otp")
                || path.endsWith("/auth/verify-phone-otp")) return "phone-otp";
        return null;
    }

    private String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}