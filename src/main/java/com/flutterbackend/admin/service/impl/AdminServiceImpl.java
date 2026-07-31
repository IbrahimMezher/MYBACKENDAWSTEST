package com.flutterbackend.admin.service.impl;

import com.flutterbackend.admin.dto.AdminLoginRequest;
import com.flutterbackend.admin.dto.AdminLoginResponse;
import com.flutterbackend.admin.dto.AdminProfileResponse;
import com.flutterbackend.admin.dto.AdminRegisterRequest;
import com.flutterbackend.admin.repository.AdminRepository;
import com.flutterbackend.admin.service.AdminService;
import com.flutterbackend.countries.domain.Countries;
import com.flutterbackend.countries.repository.CountriesRepository;
import com.flutterbackend.notifications.service.NotificationService;
import com.flutterbackend.role.domain.Role;
import com.flutterbackend.role.repository.RoleRepository;
import com.flutterbackend.user.domain.User;
import com.flutterbackend.user.domain.UserStatus;
import com.flutterbackend.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final CountriesRepository countriesRepository;
    private final com.flutterbackend.auth.service.EmailSenderService emailSenderService;
    private final com.flutterbackend.broker.repository.BrokerRepository brokerRepository;
    private final NotificationService notificationService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminServiceImpl(AdminRepository adminRepository,
                            RoleRepository roleRepository,
                            JwtUtil jwtUtil,
                            CountriesRepository countriesRepository,
                            com.flutterbackend.auth.service.EmailSenderService emailSenderService,
                            com.flutterbackend.broker.repository.BrokerRepository brokerRepository,
                            NotificationService notificationService) {
        this.adminRepository = adminRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
        this.countriesRepository = countriesRepository;
        this.emailSenderService = emailSenderService;
        this.brokerRepository = brokerRepository;
        this.notificationService = notificationService;
    }

    @Override
    public String register(AdminRegisterRequest req) {
        if (req.getPassword() == null || req.getPassword().isBlank())
            throw new RuntimeException("Password is required");
        if (!req.getPassword().equals(req.getConfirm()))
            throw new RuntimeException("Passwords do not match");
        if (adminRepository.existsByEmailAndRole_Name(req.getEmail().trim().toLowerCase(), "ADMIN"))
            throw new RuntimeException("Email already registered");

        Role adminRole = roleRepository.findByNameIgnoreCase("admin")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

        Countries country = resolveCountry(req);

        User admin = new User();
        admin.setFullName(req.getFullName());
        admin.setUsernameField(req.getUsername());
        admin.setEmail(req.getEmail().trim().toLowerCase());

        admin.setPhoneNumber(buildE164(country, req.getPhoneNumber()));
        admin.setCountry(country);
        admin.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        admin.setRole(adminRole);
        admin.setEmailVerified(true);
        admin.setPhoneVerified(true);

        admin.setTwoFaEnabled(true);
        admin.setTwoFaMethod("email");
        admin.setStatus(UserStatus.ACTIVE);
        adminRepository.save(admin);
        return "Admin account created successfully.";
    }

    private String buildE164(Countries country, String raw) {
        if (raw == null) return null;
        String num = raw.trim().replaceAll("[\\s-]", "");
        if (num.startsWith("+")) return num;
        String code = country != null && country.getCode() != null
                ? country.getCode().trim() : "";
        if (!code.startsWith("+") && !code.isEmpty()) code = "+" + code;

        if (num.startsWith("0")) num = num.substring(1);
        return code + num;
    }

    private Countries resolveCountry(AdminRegisterRequest req) {
        if (req.getCountryId() != null && req.getCountryId() > 0) {
            java.util.Optional<Countries> byId = countriesRepository.findById(req.getCountryId());
            if (byId.isPresent()) return byId.get();
        }

        if (req.getCountryName() != null && !req.getCountryName().isBlank()) {
            java.util.Optional<Countries> byName =
                    countriesRepository.findByCountryNameIgnoreCase(req.getCountryName().trim());
            if (byName.isPresent()) return byName.get();
        }

        if (req.getCountryCode() != null && !req.getCountryCode().isBlank()) {
            String code = req.getCountryCode().trim();
            java.util.Optional<Countries> byCode = countriesRepository.findByCode(code);
            if (byCode.isPresent()) return byCode.get();
            if (code.startsWith("+")) {
                byCode = countriesRepository.findByCode(code.substring(1));
            } else {
                byCode = countriesRepository.findByCode("+" + code);
            }
            if (byCode.isPresent()) return byCode.get();
        }

        throw new RuntimeException("Country not found");
    }

    @Override
    public AdminLoginResponse login(AdminLoginRequest req) {
        if (req.getEmail() == null || req.getEmail().isBlank())
            throw new RuntimeException("Email is required");
        if (req.getPassword() == null || req.getPassword().isBlank())
            throw new RuntimeException("Password is required");

        String email = req.getEmail().trim().toLowerCase();

        User admin = adminRepository.findByEmailAndRole_Name(email, "admin")
                .or(() -> adminRepository.findByEmailAndRole_Name(email, "superadmin"))
                .orElse(null);

        boolean ok = admin != null
                && admin.getPasswordHash() != null
                && passwordEncoder.matches(req.getPassword(), admin.getPasswordHash());
        if (!ok) {
            throw new RuntimeException("Invalid email or password");
        }

        boolean changed = false;
        if (!admin.isTwoFaEnabled()) {
            admin.setTwoFaEnabled(true);
            changed = true;
        }
        if (admin.getTwoFaMethod() == null || admin.getTwoFaMethod().isBlank()) {
            admin.setTwoFaMethod("email");
            changed = true;
        }
        if (admin.getPhoneNumber() != null && !admin.getPhoneNumber().isBlank()
                && !admin.isPhoneVerified()) {
            admin.setPhoneVerified(true);
            changed = true;
        }
        if (changed) adminRepository.save(admin);

        java.util.List<String> methods = new java.util.ArrayList<>();
        methods.add("email");
        if (admin.getPhoneNumber() != null && !admin.getPhoneNumber().isBlank()
                && admin.isPhoneVerified()) {
            methods.add("sms");
        }

        return AdminLoginResponse.builder()
                .accessToken(null)
                .requiresTwoFa(true)
                .tokenType("Bearer")
                .role(admin.getRole().getName())
                .fullName(admin.getFullName())
                .email(admin.getEmail())
                .phone_verified(admin.isPhoneVerified())
                .availableTwoFaMethods(methods)
                .build();
    }

    @Override
    public AdminProfileResponse getProfile(String email) {
        User admin = adminRepository.findByEmailAndRole_Name(email, "admin")
                .or(() -> adminRepository.findByEmailAndRole_Name(email, "superadmin"))
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        return AdminProfileResponse.builder()
                .adminId(admin.getUserId())
                .fullName(admin.getFullName())
                .username(admin.getUsernameField())
                .email(admin.getEmail())
                .role(admin.getRole().getName())
                .build();
    }

    @Override
    public java.util.List<com.flutterbackend.user.domain.User> getAllUsers() {
        return adminRepository.findAll();
    }

    @Override
    public com.flutterbackend.user.domain.User getUserById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public String deleteUser(Long id) {
        if (!adminRepository.existsById(id))
            throw new RuntimeException("User not found");
        adminRepository.deleteById(id);
        return "User deleted.";
    }

    @Override
    public java.util.List<com.flutterbackend.user.domain.User> getPendingBrokers() {
        return adminRepository.findByRoleNameAndStatus("broker", com.flutterbackend.user.domain.UserStatus.PENDING);
    }

    @Override
    public java.util.Map<String, Object> getBrokerDetails(Long userId) {
        com.flutterbackend.user.domain.User user = adminRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        java.util.Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("userId", user.getUserId());
        result.put("username", user.getUsername());
        result.put("fullName", user.getFullName());
        result.put("email", user.getEmail());
        result.put("phoneNumber", user.getPhoneNumber());
        result.put("emailVerified", user.isEmailVerified());
        result.put("phoneVerified", user.isPhoneVerified());
        result.put("status", user.getStatus());
        result.put("role", user.getRole() != null ? user.getRole().getName() : null);
        if (user.getCountry() != null) {
            java.util.Map<String, Object> country = new java.util.LinkedHashMap<>();
            country.put("countryId", user.getCountry().getCountryId());
            country.put("countryName", user.getCountry().getCountryName());
            country.put("code", user.getCountry().getCode());
            country.put("currency", user.getCountry().getCurrency());
            result.put("country", country);
        }
        result.put("createdAt", user.getCreatedAt());
        result.put("updatedAt", user.getUpdatedAt());

        brokerRepository.findByUser_UserId(user.getUserId()).ifPresent(b -> {
            java.util.Map<String, Object> broker = new java.util.LinkedHashMap<>();
            broker.put("brokerId", b.getBrokerId());
            broker.put("companyName", b.getCompanyName());
            broker.put("licenseNumber", b.getLicenseNumber());
            broker.put("address", b.getAddress());
            broker.put("taxId", b.getTaxId());
            broker.put("websiteUrl", b.getWebsiteUrl());
            broker.put("logoUrl", b.getLogoUrl());
            broker.put("idFrontUrl", b.getIdFrontUrl());
            broker.put("idBackUrl", b.getIdBackUrl());
            broker.put("createdAt", b.getCreatedAt());
            result.put("broker", broker);
        });
        return result;
    }

    @Override
    public String updateBrokerStatus(Long userId, String newStatus) {
        com.flutterbackend.user.domain.User user = adminRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        com.flutterbackend.user.domain.UserStatus status =
                com.flutterbackend.user.domain.UserStatus.valueOf(newStatus);

        if (status == com.flutterbackend.user.domain.UserStatus.ACTIVE
                && !user.isEmailVerified()) {
            throw new RuntimeException(
                    "This broker has not verified their email yet and cannot be approved.");
        }

        user.setStatus(status);
        adminRepository.save(user);
        if (status == com.flutterbackend.user.domain.UserStatus.ACTIVE) {
            notificationService.create(
                    user,
                    "Broker account approved",
                    "Your broker account has been approved. You can now list policies.",
                    "ACCOUNT");
        } else if (status == com.flutterbackend.user.domain.UserStatus.SUSPENDED) {
            notificationService.create(
                    user,
                    "Broker account update",
                    "Your broker account application was not approved at this time.",
                    "ACCOUNT");
        }

        try {
            String name = user.getFullName() != null ? user.getFullName() : "Broker";
            if (status == com.flutterbackend.user.domain.UserStatus.ACTIVE) {
                emailSenderService.sendEmail(
                        user.getEmail(),
                        "Your broker account has been approved",
                        "Dear " + name + ",\n\n"
                                + "Good news — your broker account has been approved. "
                                + "You can now sign in and start listing policies.\n\n"
                                + "I.A. Insurance Team");
            } else if (status == com.flutterbackend.user.domain.UserStatus.SUSPENDED) {
                emailSenderService.sendEmail(
                        user.getEmail(),
                        "Update on your broker account",
                        "Dear " + name + ",\n\n"
                                + "We're sorry to inform you that your broker account "
                                + "application was not approved at this time. "
                                + "Please contact support if you have questions.\n\n"
                                + "I.A. Insurance Team");
            }
        } catch (Exception ignored) {}

        return "Broker status updated to " + newStatus;
    }

}
