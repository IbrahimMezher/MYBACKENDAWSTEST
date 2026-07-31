package com.flutterbackend.user.service;

import com.flutterbackend.countries.domain.Countries;
import com.flutterbackend.countries.repository.CountriesRepository;
import com.flutterbackend.role.domain.Role;
import com.flutterbackend.role.repository.RoleRepository;
import com.flutterbackend.user.domain.User;
import com.flutterbackend.user.domain.UserStatus;
import com.flutterbackend.user.dto.UserLoginRequest;
import com.flutterbackend.user.dto.UserLoginResponse;
import com.flutterbackend.user.dto.UserSignupRequest;
import com.flutterbackend.user.repository.UserRepository;
import com.flutterbackend.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final CountriesRepository countriesRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final com.flutterbackend.security.ratelimit.RateLimitService rateLimitService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final Pattern PASSWORD_REGEX = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])[\\S]{8,}$"
    );

    public UserService(UserRepository userRepository,
                       CountriesRepository countriesRepository,
                       RoleRepository roleRepository,
                       JwtUtil jwtUtil,
                       com.flutterbackend.security.ratelimit.RateLimitService rateLimitService) {
        this.userRepository = userRepository;
        this.countriesRepository = countriesRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
        this.rateLimitService = rateLimitService;
    }

    public UserLoginResponse signup(UserSignupRequest body) {
        if (body.fullName == null || body.fullName.isBlank())
            throw new RuntimeException("Full name is required");
        if (body.email == null || body.email.isBlank())
            throw new RuntimeException("Email is required");
        if (body.password == null || body.password.isBlank())
            throw new RuntimeException("Password is required");
        if (body.confirm == null || body.confirm.isBlank())
            throw new RuntimeException("Password confirmation is required");
        if (!PASSWORD_REGEX.matcher(body.password).matches())
            throw new RuntimeException("Weak password: must be 8+ chars with uppercase, lowercase, digit, and special char");
        if (!body.password.equals(body.confirm))
            throw new RuntimeException("Passwords do not match");
        if (!Boolean.TRUE.equals(body.acceptedTerms))
            throw new RuntimeException("You must accept the terms and conditions.");
        if (userRepository.existsByEmail(body.email.trim().toLowerCase()))
            throw new RuntimeException("Email already registered");

        Countries country = countriesRepository.findById(body.countryId)
                .orElseThrow(() -> new RuntimeException("Country not found"));

        Role customerRole = roleRepository.findByNameIgnoreCase("customer")
                .orElseThrow(() -> new RuntimeException("Customer role not found"));

        User user = new User();
        user.setFullName(body.fullName.trim());
        user.setEmail(body.email.trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(body.password));
        user.setPhoneNumber(buildE164(country, body.phoneNumber));
        user.setRole(customerRole);
        user.setCountry(country);
        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().getName());

        UserLoginResponse res = new UserLoginResponse();
        res.accessToken = token;
        res.tokenType = "Bearer";
        res.role = user.getRole().getName();
        res.fullName = user.getFullName();
        res.email = user.getEmail();
        res.email_verified = false;
        res.phone_verified = false;
        res.status = user.getStatus();
        if (user.getCountry() != null) res.countryId = user.getCountry().getCountryId();
        return res;
    }

    public UserLoginResponse login(UserLoginRequest body) {
        if (body.email == null || body.email.isBlank())
            throw new RuntimeException("Email is required");
        if (body.password == null || body.password.isBlank())
            throw new RuntimeException("Password is required");

        String account = body.email.trim().toLowerCase();

        rateLimitService.assertAccountNotLocked(account);

        User user = userRepository.findByEmail(account).orElse(null);

        boolean authenticated = false;
        if (user != null && user.getPasswordHash() != null) {
            try {
                authenticated = passwordEncoder.matches(body.password, user.getPasswordHash());
            } catch (IllegalArgumentException ignored) {
                authenticated = false;
            }
        }

        if (!authenticated) {
            boolean justLocked = rateLimitService.recordAccountFailure(account);
            if (justLocked) {
                // This attempt is the one that crossed the failure threshold —
                // report the lockout immediately instead of a generic
                // "Invalid email or password", so the response the user sees
                // is consistent from this point forward regardless of whether
                // the password they type next is actually correct.
                rateLimitService.assertAccountNotLocked(account);
            }
            throw new RuntimeException("Invalid email or password");
        }

        if (user.getStatus() == UserStatus.SUSPENDED)
            throw new RuntimeException("Your account has been suspended. Please contact support.");
        if (user.getStatus() == UserStatus.PENDING)
            throw new RuntimeException("Your account is pending admin approval.");
        if (user.getStatus() == UserStatus.DELETED)
            throw new RuntimeException("This account has been deleted.");

        if (user.getStatus() == UserStatus.DEACTIVATED) {
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
        }

        rateLimitService.recordAccountSuccess(account);

        if (user.isTwoFaEnabled()) {

            java.util.List<String> methods = new java.util.ArrayList<>();
            methods.add("email");
            if (user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank()
                    && user.isPhoneVerified()) {
                methods.add("sms");
            }

            UserLoginResponse challenge = new UserLoginResponse();
            challenge.accessToken = null;
            challenge.requiresTwoFa = true;
            challenge.twoFaEnabled = true;

            String preferred = user.getTwoFaMethod();
            if (preferred == null || !methods.contains(preferred)) {
                preferred = methods.get(0);
            }
            challenge.twoFaMethod = preferred;
            challenge.availableTwoFaMethods = methods;
            challenge.email = user.getEmail();
            challenge.fullName = user.getFullName();
            challenge.role = user.getRole().getName();
            challenge.phone_verified = user.isPhoneVerified();
            challenge.email_verified = user.isEmailVerified();

            return challenge;
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().getName());

        UserLoginResponse res = new UserLoginResponse();
        res.accessToken = token;
        res.requiresTwoFa = false;
        res.tokenType = "Bearer";
        res.role = user.getRole().getName();
        res.fullName = user.getFullName();
        res.email = user.getEmail();
        res.email_verified = user.isEmailVerified();
        res.phone_verified = user.isPhoneVerified();
        res.status = user.getStatus();
        res.twoFaEnabled = user.isTwoFaEnabled();
        res.twoFaMethod = user.getTwoFaMethod();
        if (user.getCountry() != null) res.countryId = user.getCountry().getCountryId();
        return res;
    }

    public java.util.Map<String, Object> getUserSummary(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        java.util.Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("userId", user.getUserId().toString());
        result.put("fullName", user.getFullName());
        result.put("email", user.getEmail());
        result.put("phoneNumber", user.getPhoneNumber());
        return result;
    }

    private String buildE164(Countries country, String raw) {
        if (raw == null || raw.isBlank()) return null;
        String num = raw.trim().replaceAll("[\\s-]", "");
        if (num.startsWith("+")) return num;
        String code = (country != null && country.getCode() != null)
                ? country.getCode().trim() : "";
        if (!code.isEmpty() && !code.startsWith("+")) code = "+" + code;
        if (num.startsWith("0")) num = num.substring(1);
        return code + num;
    }

}