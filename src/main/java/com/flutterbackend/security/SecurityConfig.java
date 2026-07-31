package com.flutterbackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final String allowedOrigins;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter,
                          @Value("${app.cors.allowed-origins}") String allowedOrigins) {
        this.jwtFilter = jwtFilter;
        this.allowedOrigins = allowedOrigins;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/users/signup",
                                "/users/login",
                                "/brokers/signup",
                                "/uploads/public/**",
                                "/auth/forgot-password",
                                "/auth/reset-password",
                                "/auth/resetpassword",
                                "/auth/2fa/send",
                                "/auth/2fa/verify",
                                "/admin/auth/login",
                                "/countries/getAllCountries",
                                "/admin/auth/2fa/send",
                                "/admin/auth/2fa/verify"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/brokers/public",
                                "/policies/public/broker/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.PUT,
                                "/transactions/*/broker-approve",
                                "/transactions/*/broker-reject"
                        ).hasAnyRole("broker", "admin", "superadmin")

                        .requestMatchers(
                                "/brokers/broker/**",
                                "/brokers/brokerinfo",
                                "/brokers/brokerId",
                                "/broker/claims",
                                "/broker/claims/**",
                                "/policies/broker",
                                "/policies/broker/**",
                                "/policies/create"
                        ).hasAnyRole("broker", "admin", "superadmin")

                        .requestMatchers(
                                "/cart/**",
                                "/wishlist/**",
                                "/checkout",
                                "/addresses/**",
                                "/transactions/create",
                                "/transactions/users",
                                "/claims/create",
                                "/claims/user/me"
                        ).hasAnyRole("customer", "admin", "superadmin")

                        .requestMatchers(HttpMethod.POST, "/reviews/create")
                        .hasAnyRole("customer", "admin", "superadmin")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
        config.setAllowedOriginPatterns(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
