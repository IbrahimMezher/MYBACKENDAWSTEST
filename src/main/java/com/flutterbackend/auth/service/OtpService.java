package com.flutterbackend.auth.service;

import com.flutterbackend.user.domain.User;
import com.flutterbackend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;

    public OtpService(UserRepository userRepository, EmailSenderService emailSenderService) {
        this.userRepository = userRepository;
        this.emailSenderService = emailSenderService;
    }

    public void SendGenerateOtp(User user) {
        String otp = String.format("%06d", new Random().nextInt(999999));

        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        emailSenderService.sendEmail(
                user.getEmail(),
                "Your verification OTP",
                "Your OTP code is: " + otp + "\nIt expires in 10 minutes."
        );
    }
}
