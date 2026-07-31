package com.flutterbackend.auth.service;

import com.flutterbackend.user.domain.User;
import com.flutterbackend.user.repository.UserRepository;
import com.flutterbackend.util.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.HttpRequestHandlerServlet;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class PasswordResetService {

    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final CurrentUser currentUser;

    public PasswordResetService(UserRepository userRepository,
                                EmailSenderService emailSenderService, CurrentUser currentUser) {
        this.userRepository = userRepository;
        this.emailSenderService = emailSenderService;
        this.currentUser = currentUser;
    }

    public String requestReset(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Email not found"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        emailSenderService.sendEmail(
                user.getEmail(),
                "Password Reset Request",
                "Your password reset token is: " + token
                        + "\nThis token expires in 15 minutes."
        );

        return "Password reset email sent.";
    }

    public String confirmReset(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            throw new RuntimeException("Token has expired. Please request a new one.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        return "Password reset successfully.";
    }

    public String PasswordReset(String Password, String NewPassword,
                              String Confirm,
                              HttpServletRequest request)
    {
        User user = currentUser.getCurrentUser(request);
        if (!passwordEncoder.matches(Password, user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        if (!NewPassword.equals(Confirm)) {
            throw new RuntimeException("New password and confirmation do not match");
        }

        user.setPasswordHash(passwordEncoder.encode(NewPassword));
        userRepository.save(user);

        return "Password changed successfully.";
    }
}
