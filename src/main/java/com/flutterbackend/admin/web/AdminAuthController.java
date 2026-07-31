package com.flutterbackend.admin.web;

import com.flutterbackend.admin.dto.AdminLoginRequest;
import com.flutterbackend.admin.dto.AdminLoginResponse;
import com.flutterbackend.admin.dto.AdminProfileResponse;
import com.flutterbackend.admin.dto.AdminRegisterRequest;
import com.flutterbackend.admin.repository.AdminRepository;
import com.flutterbackend.admin.service.AdminService;
import com.flutterbackend.auth.service.EmailSenderService;
import com.flutterbackend.util.TwilioService;
import com.flutterbackend.user.domain.User;
import com.flutterbackend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/admin/auth")
@CrossOrigin(origins = "*")
public class AdminAuthController {

    private final AdminService adminService;
    private final JwtUtil jwtUtil;
    private final AdminRepository adminRepository;
    private final EmailSenderService emailSenderService;
    private final TwilioService twilioService;

    public AdminAuthController(AdminService adminService,
                               JwtUtil jwtUtil,
                               AdminRepository adminRepository,
                               EmailSenderService emailSenderService,
                               TwilioService twilioService) {
        this.adminService = adminService;
        this.jwtUtil = jwtUtil;
        this.adminRepository = adminRepository;
        this.emailSenderService = emailSenderService;
        this.twilioService = twilioService;
    }

    @PreAuthorize("hasAnyRole('superadmin')")
    @PostMapping("/register")
    public String register(@RequestBody AdminRegisterRequest request) {
        return adminService.register(request);
    }

    @PostMapping("/login")
    public AdminLoginResponse login(@RequestBody AdminLoginRequest request) {
        return adminService.login(request);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('admin','superadmin')")
    public AdminProfileResponse me(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        return adminService.getProfile(email);
    }

    @PostMapping("/2fa/send")
    public String sendAdmin2FA(@RequestBody Map<String, String> body) {
        String email  = body.get("email");
        String method = body.getOrDefault("method", "email");
        if (email == null || email.isBlank())
            throw new RuntimeException("Email is required");

        User admin = adminRepository.findByEmailAndRole_Name(email.trim().toLowerCase(), "admin")
                .or(() -> adminRepository.findByEmailAndRole_Name(email.trim().toLowerCase(), "superadmin"))
                .orElseThrow(() -> new RuntimeException("Admin account not found"));

        String otp = String.format("%06d", new Random().nextInt(999999));
        admin.setTwoFaOtp(otp);
        admin.setTwoFaOtpExpiry(LocalDateTime.now().plusMinutes(10));
        adminRepository.save(admin);

        if ("sms".equalsIgnoreCase(method) && admin.getPhoneNumber() != null && !admin.getPhoneNumber().isBlank()) {
            twilioService.sendOtp(admin.getPhoneNumber());
        } else {
            emailSenderService.sendEmail(
                    admin.getEmail(),
                    "Admin Login Verification Code",
                    "Your admin 2FA code is: " + otp + "\nIt expires in 10 minutes.\n\nDo not share this code with anyone."
            );
        }

        return "2FA code sent to admin email.";
    }

    @PostMapping("/2fa/verify")
    public AdminLoginResponse verifyAdmin2FA(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code  = body.get("code");
        if (email == null || email.isBlank()) throw new RuntimeException("Email is required");
        if (code  == null || code.isBlank())  throw new RuntimeException("Code is required");

        User admin = adminRepository.findByEmailAndRole_Name(email.trim().toLowerCase(), "admin")
                .or(() -> adminRepository.findByEmailAndRole_Name(email.trim().toLowerCase(), "superadmin"))
                .orElseThrow(() -> new RuntimeException("Admin account not found"));

        String method = body.getOrDefault("method", "email");
        if ("sms".equalsIgnoreCase(method)
                && admin.getPhoneNumber() != null && !admin.getPhoneNumber().isBlank()) {
            boolean verified = twilioService.verifyOtp(admin.getPhoneNumber(), code.trim());
            if (!verified) throw new RuntimeException("Invalid 2FA code.");
        } else {
            if (admin.getTwoFaOtp() == null || admin.getTwoFaOtpExpiry() == null)
                throw new RuntimeException("No 2FA code was requested. Please request a new one.");
            if (LocalDateTime.now().isAfter(admin.getTwoFaOtpExpiry()))
                throw new RuntimeException("2FA code has expired. Please request a new one.");
            if (!code.trim().equals(admin.getTwoFaOtp()))
                throw new RuntimeException("Invalid 2FA code.");
        }

        admin.setTwoFaOtp(null);
        admin.setTwoFaOtpExpiry(null);
        adminRepository.save(admin);

        String token = jwtUtil.generateToken(admin.getEmail(), admin.getRole().getName());
        return AdminLoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .role(admin.getRole().getName())
                .fullName(admin.getFullName())
                .email(admin.getEmail())
                .build();
    }
}
