package com.flutterbackend.auth.web;

import com.flutterbackend.auth.dto.ForgotPasswordRequest;
import com.flutterbackend.auth.dto.OtpRequest;
import com.flutterbackend.auth.dto.PasswordResetRequest;
import com.flutterbackend.auth.dto.ResetPasswordRequest;
import com.flutterbackend.auth.service.EmailSenderService;
import com.flutterbackend.auth.service.OtpService;
import com.flutterbackend.auth.service.PasswordResetService;
import com.flutterbackend.user.domain.User;
import com.flutterbackend.user.dto.UserLoginResponse;
import com.flutterbackend.user.repository.UserRepository;
import com.flutterbackend.util.CurrentUser;
import com.flutterbackend.util.JwtUtil;
import com.flutterbackend.util.TwilioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final OtpService otpService;
    private final PasswordResetService passwordResetService;
    private final CurrentUser currentUser;
    private final UserRepository userRepository;
    private final TwilioService twilioService;
    private final EmailSenderService emailSenderService;
    private final JwtUtil jwtUtil;
    private final com.flutterbackend.account.service.AccountService accountService;

    public AuthController(OtpService otpService,
                          PasswordResetService passwordResetService,
                          CurrentUser currentUser,
                          UserRepository userRepository,
                          TwilioService twilioService,
                          EmailSenderService emailSenderService,
                          JwtUtil jwtUtil,
                          com.flutterbackend.account.service.AccountService accountService) {
        this.otpService = otpService;
        this.passwordResetService = passwordResetService;
        this.currentUser = currentUser;
        this.userRepository = userRepository;
        this.twilioService = twilioService;
        this.emailSenderService = emailSenderService;
        this.jwtUtil = jwtUtil;
        this.accountService = accountService;
    }

    @PostMapping("/get_otp")
    public String sendOtp(HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        otpService.SendGenerateOtp(user);
        return "OTP sent successfully.";
    }

    @PostMapping("/verify_otp")
    public String verifyOtp(@RequestBody OtpRequest body, HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        if (user.getOtp() == null || user.getOtpExpiry() == null)
            throw new RuntimeException("No OTP requested, please request one first");
        if (LocalDateTime.now().isAfter(user.getOtpExpiry()))
            throw new RuntimeException("OTP expired, please request a new one");
        if (!body.getOtp().equals(user.getOtp()))
            throw new RuntimeException("Wrong OTP");
        user.setEmailVerified(true);
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
        return "Email verified successfully.";
    }

    @PostMapping("/change-email")
    public Map<String, String> changeEmail(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String newEmail = body.get("email");
        if (newEmail == null || newEmail.isBlank())
            throw new RuntimeException("Email is required");
        newEmail = newEmail.trim().toLowerCase();
        if (userRepository.existsByEmail(newEmail))
            throw new RuntimeException("Email already in use");
        User user = currentUser.getCurrentUser(request);
        user.setEmail(newEmail);
        user.setEmailVerified(false);
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
        String role = user.getRole() != null ? user.getRole().getName() : "customer";
        String newToken = jwtUtil.generateToken(newEmail, role);
        Map<String, String> res = new java.util.LinkedHashMap<>();
        res.put("message", "Email updated successfully.");
        res.put("accessToken", newToken);
        res.put("email", newEmail);
        return res;
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody ForgotPasswordRequest body) {
        return passwordResetService.requestReset(body.getEmail());
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordRequest body) {
        return passwordResetService.confirmReset(body.getToken(), body.getNewPassword());
    }

    @PostMapping("/send-phone-otp")
    public String sendPhoneOtp(@RequestBody Map<String, String> body,
                               HttpServletRequest request) {

        User user = currentUser.getCurrentUser(request);
        String phoneNumber = user.getPhoneNumber();
        if (phoneNumber == null || phoneNumber.isBlank()) {

            phoneNumber = body.get("phoneNumber");
            if (phoneNumber == null || phoneNumber.isBlank())
                throw new RuntimeException("No phone number on file. Please add one first.");
        }
        twilioService.sendOtp(phoneNumber);
        return "OTP sent to " + phoneNumber;
    }

    @PostMapping("/verify-phone-otp")
    public String verifyPhoneOtp(@RequestBody Map<String, String> body,
                                 HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        String code = body.get("code");
        String phoneNumber = user.getPhoneNumber();
        if (phoneNumber == null || phoneNumber.isBlank())
            phoneNumber = body.get("phoneNumber");
        if (phoneNumber == null || phoneNumber.isBlank())
            throw new RuntimeException("No phone number on file.");
        boolean verified = twilioService.verifyOtp(phoneNumber, code);
        if (!verified)
            throw new RuntimeException("Invalid OTP code");
        user.setPhoneVerified(true);

        if (user.getPhoneNumber() == null || user.getPhoneNumber().isBlank())
            user.setPhoneNumber(phoneNumber);
        userRepository.save(user);
        return "Phone verified successfully!";
    }

    @PostMapping("/resetpassword")
    public String resetPasswordLoggedIn(@RequestBody PasswordResetRequest body, HttpServletRequest request) {
        return passwordResetService.PasswordReset(body.getPassword(), body.getNewPassword(), body.getConfirm(), request);
    }

    @PostMapping("/2fa/setup")
    public String setup2FA(@RequestBody Map<String, String> body, HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        String method = body.get("method");
        if (method == null || (!method.equals("email") && !method.equals("sms")))
            throw new RuntimeException("Invalid 2FA method. Use 'email' or 'sms'");
        if (method.equals("sms") && (user.getPhoneNumber() == null || !user.isPhoneVerified()))
            throw new RuntimeException("You must verify your phone number before enabling SMS 2FA");
        user.setTwoFaEnabled(true);
        user.setTwoFaMethod(method);
        userRepository.save(user);
        return "Two-factor authentication enabled via " + method;
    }

    @PostMapping("/2fa/disable")
    public String disable2FA(HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);

        String role = user.getRole() != null ? user.getRole().getName() : "";
        if ("admin".equalsIgnoreCase(role) || "superadmin".equalsIgnoreCase(role)) {
            throw new RuntimeException("Two-factor authentication is required for admin accounts and cannot be disabled.");
        }
        user.setTwoFaEnabled(false);
        user.setTwoFaMethod(null);
        user.setTwoFaOtp(null);
        user.setTwoFaOtpExpiry(null);
        userRepository.save(user);
        return "Two-factor authentication disabled.";
    }

    @PostMapping("/2fa/send")
    public String send2FACode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank())
            throw new RuntimeException("Email is required");
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isTwoFaEnabled())
            throw new RuntimeException("2FA is not enabled for this account");

        String requested = body.get("method");
        String method = (requested != null && !requested.isBlank())
                ? requested
                : user.getTwoFaMethod();
        if (method == null) method = "email";
        if ("sms".equals(method)
                && (user.getPhoneNumber() == null || !user.isPhoneVerified())) {
            method = "email";
        }

        if ("sms".equals(method)) {

            twilioService.sendOtp(user.getPhoneNumber());
            user.setTwoFaOtp(null);
            user.setTwoFaOtpExpiry(null);
            userRepository.save(user);
        } else {
            String otp = String.format("%06d", new Random().nextInt(1000000));
            user.setTwoFaOtp(otp);
            user.setTwoFaOtpExpiry(LocalDateTime.now().plusMinutes(10));
            userRepository.save(user);
            emailSenderService.sendEmail(
                    user.getEmail(),
                    "Your Login Verification Code",
                    "Your 2FA code is: " + otp + "\nIt expires in 10 minutes."
            );
        }
        return "2FA code sent via " + method;
    }

    @PostMapping("/2fa/verify")
    public UserLoginResponse verify2FACode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");
        if (email == null || email.isBlank()) throw new RuntimeException("Email is required");
        if (code == null || code.isBlank()) throw new RuntimeException("Code is required");

        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String requested = body.get("method");
        String method = (requested != null && !requested.isBlank())
                ? requested
                : user.getTwoFaMethod();
        if (method == null) method = "email";
        if ("sms".equals(method)
                && (user.getPhoneNumber() == null || !user.isPhoneVerified())) {
            method = "email";
        }

        if ("sms".equals(method)) {
            boolean verified = twilioService.verifyOtp(user.getPhoneNumber(), code);
            if (!verified) throw new RuntimeException("Invalid 2FA code");
        } else {
            if (user.getTwoFaOtp() == null || user.getTwoFaOtpExpiry() == null)
                throw new RuntimeException("No 2FA code requested");
            if (LocalDateTime.now().isAfter(user.getTwoFaOtpExpiry()))
                throw new RuntimeException("2FA code expired");
            if (!code.equals(user.getTwoFaOtp()))
                throw new RuntimeException("Invalid 2FA code");
        }

        user.setTwoFaOtp(null);
        user.setTwoFaOtpExpiry(null);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().getName());
        UserLoginResponse res = new UserLoginResponse();
        res.accessToken = token;
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

    @GetMapping("/2fa/status")
    public Map<String, Object> get2FAStatus(HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        java.util.List<String> methods = new java.util.ArrayList<>();
        methods.add("email");
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank()
                && user.isPhoneVerified()) {
            methods.add("sms");
        }
        return Map.of(
                "twoFaEnabled", user.isTwoFaEnabled(),
                "twoFaMethod", user.getTwoFaMethod() != null ? user.getTwoFaMethod() : "",
                "emailVerified", user.isEmailVerified(),
                "phoneVerified", user.isPhoneVerified(),
                "availableTwoFaMethods", methods
        );
    }

    @GetMapping("/status")
    public Map<String, Object> getUserStatus(HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        java.util.List<String> methods = new java.util.ArrayList<>();
        methods.add("email");
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank()
                && user.isPhoneVerified()) {
            methods.add("sms");
        }

        java.util.Map<String, Object> res = new java.util.LinkedHashMap<>();
        res.put("userId", user.getUserId());
        res.put("status", user.getStatus().toString());
        res.put("email", user.getEmail());
        res.put("fullName", user.getFullName());
        res.put("emailVerified", user.isEmailVerified());
        res.put("phoneVerified", user.isPhoneVerified());
        res.put("role", user.getRole() != null ? user.getRole().getName() : "");
        res.put("twoFaEnabled", user.isTwoFaEnabled());
        res.put("twoFaMethod", user.getTwoFaMethod() != null ? user.getTwoFaMethod() : "");
        res.put("availableTwoFaMethods", methods);
        if (user.getCountry() != null) res.put("countryId", user.getCountry().getCountryId());
        return res;
    }

    @PostMapping("/privacy")
    public String updatePrivacy(@RequestBody Map<String, Boolean> body, HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        if (body.containsKey("profileVisible"))
            user.setPrivacyProfileVisible(body.get("profileVisible"));
        if (body.containsKey("contactVisible"))
            user.setPrivacyContactVisible(body.get("contactVisible"));
        userRepository.save(user);
        return "Privacy settings updated.";
    }

    @GetMapping("/privacy")
    public Map<String, Object> getPrivacy(HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return Map.of(
                "profileVisible", user.isPrivacyProfileVisible(),
                "contactVisible", user.isPrivacyContactVisible()
        );
    }

    @PostMapping("/account/request-action")
    public String requestAccountAction(@RequestBody Map<String, String> body,
                                       HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return accountService.requestActionCode(user, body.get("action"));
    }

    @PostMapping("/account/confirm-action")
    public String confirmAccountAction(@RequestBody Map<String, String> body,
                                       HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return accountService.confirmAction(user, body.get("code"));
    }
}
