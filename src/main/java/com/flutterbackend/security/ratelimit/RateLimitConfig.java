package com.flutterbackend.security.ratelimit;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RateLimitConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;

    public RateLimitConfig(RateLimitInterceptor rateLimitInterceptor) {
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns(
                        "/users/login",
                        "/users/signup",
                        "/brokers/signup",
                        "/admin/auth/login",
                        "/admin/auth/2fa/**",
                        "/auth/get_otp",
                        "/auth/verify_otp",
                        "/auth/forgot-password",
                        "/auth/reset-password",
                        "/auth/2fa/**",
                        "/auth/send-phone-otp",
                        "/auth/verify-phone-otp");
    }
}
