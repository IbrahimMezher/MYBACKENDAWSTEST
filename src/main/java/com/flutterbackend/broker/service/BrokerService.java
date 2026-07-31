package com.flutterbackend.broker.service;

import com.flutterbackend.auth.service.EmailSenderService;
import com.flutterbackend.broker.domain.Broker;
import com.flutterbackend.broker.dto.BrokerDetails;
import com.flutterbackend.broker.dto.BrokerResponse;
import com.flutterbackend.broker.dto.BrokerSignupRequest;
import com.flutterbackend.broker.repository.BrokerRepository;
import com.flutterbackend.eveythingPolicies.policies.repository.PoliciesRepository;
import com.flutterbackend.countries.domain.Countries;
import com.flutterbackend.countries.repository.CountriesRepository;
import com.flutterbackend.role.domain.Role;
import com.flutterbackend.role.repository.RoleRepository;
import com.flutterbackend.user.domain.User;
import com.flutterbackend.user.domain.UserStatus;
import com.flutterbackend.user.dto.UserLoginResponse;
import com.flutterbackend.user.repository.UserRepository;
import com.flutterbackend.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;
import java.util.List;

@Service
@Transactional
public class BrokerService {

    private final BrokerRepository brokerRepository;
    private final UserRepository userRepository;
    private final CountriesRepository countriesRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;
    private final EmailSenderService emailSenderService;
    private final PoliciesRepository policiesRepository;

    private static final Pattern PASSWORD_REGEX = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_#])[A-Za-z\\d@$!%*?&_#]{8,}$"
    );

    public BrokerService(BrokerRepository brokerRepository,
                         UserRepository userRepository,
                         CountriesRepository countriesRepository,
                         RoleRepository roleRepository,
                         JwtUtil jwtUtil,
                         EmailSenderService emailSenderService,
                         PoliciesRepository policiesRepository) {
        this.brokerRepository = brokerRepository;
        this.userRepository = userRepository;
        this.countriesRepository = countriesRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
        this.emailSenderService = emailSenderService;
        this.policiesRepository = policiesRepository;
    }

    public UserLoginResponse signup(BrokerSignupRequest body) {
        if (body.getFullName() == null || body.getFullName().isBlank())
            throw new RuntimeException("Full name is required");
        if (body.getEmail() == null || body.getEmail().isBlank())
            throw new RuntimeException("Email is required");
        if (body.getPassword() == null || body.getPassword().isBlank())
            throw new RuntimeException("Password is required");
        if (body.getConfirm() == null || body.getConfirm().isBlank())
            throw new RuntimeException("Password confirmation is required");
        if (!PASSWORD_REGEX.matcher(body.getPassword()).matches())
            throw new RuntimeException("Weak password: must be 8+ chars with uppercase, lowercase, digit, and special char");
        if (!body.getPassword().equals(body.getConfirm()))
            throw new RuntimeException("Passwords do not match");
        if (!Boolean.TRUE.equals(body.getAcceptedTerms()))
            throw new RuntimeException("You must accept the terms and conditions.");
        if (userRepository.existsByEmail(body.getEmail().trim().toLowerCase()))
            throw new RuntimeException("Email already registered");
        if (body.getCompanyName() == null || body.getCompanyName().isBlank())
            throw new RuntimeException("Company name is required");
        if (body.getLicenseNumber() == null || body.getLicenseNumber().isBlank())
            throw new RuntimeException("License number is required");
        if (body.getLogoUrl() == null || body.getLogoUrl().isBlank())
            throw new RuntimeException("Company logo is required");
        if (body.getIdFrontUrl() == null || body.getIdFrontUrl().isBlank())
            throw new RuntimeException("ID front document is required");
        if (body.getIdBackUrl() == null || body.getIdBackUrl().isBlank())
            throw new RuntimeException("ID back document is required");

        Countries country = countriesRepository.findById(body.getCountryId())
                .orElseThrow(() -> new RuntimeException("Country not found"));

        Role brokerRole = roleRepository.findByNameIgnoreCase("broker")
                .orElseThrow(() -> new RuntimeException("Broker role not found"));

        User user = new User();
        user.setFullName(body.getFullName().trim());
        user.setEmail(body.getEmail().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(body.getPassword()));
        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        user.setRole(brokerRole);
        user.setCountry(country);
        user.setStatus(UserStatus.PENDING);
        user.setPhoneNumber(buildE164(country, body.getPhoneNumber()));

        User savedUser = userRepository.save(user);

        Broker broker = new Broker();
        broker.setUser(savedUser);
        broker.setCompanyName(body.getCompanyName().trim());
        broker.setLicenseNumber(body.getLicenseNumber().trim());
        broker.setAddress(body.getAddress());
        broker.setTaxId(body.getTaxId());
        broker.setWebsiteUrl(body.getWebsiteUrl());
        broker.setLogoUrl(body.getLogoUrl());
        broker.setIdFrontUrl(body.getIdFrontUrl());
        broker.setIdBackUrl(body.getIdBackUrl());
        brokerRepository.save(broker);

        try {
            emailSenderService.sendEmail(
                savedUser.getEmail(),
                "Broker Registration Received",
                "Dear " + savedUser.getFullName() + ",\n\n"
                + "Thank you for registering as a broker on our platform.\n\n"
                + "Your account is currently under review. You will be notified by email once an admin approves or rejects your application.\n\n"
                + "I.A. Insurance Team"
            );
        } catch (Exception ignored) {}

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
        return res;
    }

    public Broker getBrokerByUserId(Long userId) {
        return brokerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Broker not found"));
    }

    public BrokerDetails.BrokerDetailsBuilder getBrokerByBrokerId(Long brokerid) {
        Broker broker = brokerRepository.findById(brokerid)
                .orElseThrow(() -> new RuntimeException("Broker not found"));
        return BrokerDetails.builder()
                .createdAt(broker.getCreatedAt())
                .websiteUrl(broker.getWebsiteUrl())
                .logoUrl(broker.getLogoUrl())
                .idFrontUrl(broker.getIdFrontUrl())
                .idBackUrl(broker.getIdBackUrl())
                .companyName(broker.getCompanyName())
                .licenseNumber(broker.getLicenseNumber());
    }

    public List<BrokerResponse> getActiveBrokerDirectory() {
        return brokerRepository.findAll().stream()
                .filter(b -> b.getUser() != null
                        && b.getUser().getStatus() == UserStatus.ACTIVE)
                .map(b -> {
                    long activeCount = policiesRepository.findByBroker_BrokerId(b.getBrokerId())
                            .stream()
                            .filter(p -> "ACTIVE".equalsIgnoreCase(p.getStatus()))
                            .count();
                    return BrokerResponse.builder()
                            .brokerId(b.getBrokerId())
                            .userId(b.getUser().getUserId())
                            .fullName(b.getUser().getFullName())
                            .companyName(b.getCompanyName())
                            .licenseNumber(b.getLicenseNumber())
                            .logoUrl(b.getLogoUrl())
                            .activePolicyCount(activeCount)
                            .build();
                })
                .filter(b -> b.getActivePolicyCount() > 0)
                .toList();
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
