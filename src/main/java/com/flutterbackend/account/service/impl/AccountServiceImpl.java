package com.flutterbackend.account.service.impl;

import com.flutterbackend.account.service.AccountService;
import com.flutterbackend.auth.service.EmailSenderService;
import com.flutterbackend.broker.repository.BrokerRepository;
import com.flutterbackend.eveythingPolicies.policies.repository.PoliciesRepository;
import com.flutterbackend.user.domain.User;
import com.flutterbackend.user.domain.UserStatus;
import com.flutterbackend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;
    private final BrokerRepository brokerRepository;
    private final PoliciesRepository policiesRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    private static final int CODE_TTL_MINUTES = 10;

    public AccountServiceImpl(UserRepository userRepository,
                              EmailSenderService emailSenderService,
                              BrokerRepository brokerRepository,
                              PoliciesRepository policiesRepository) {
        this.userRepository = userRepository;
        this.emailSenderService = emailSenderService;
        this.brokerRepository = brokerRepository;
        this.policiesRepository = policiesRepository;
    }

    @Override
    public String requestActionCode(User user, String action) {
        String normalized = action == null ? "" : action.trim().toUpperCase();
        if (!normalized.equals("DEACTIVATE") && !normalized.equals("DELETE")) {
            throw new RuntimeException("Invalid account action");
        }

        String code = String.format("%06d", secureRandom.nextInt(1_000_000));
        user.setAccountActionOtp(code);
        user.setAccountActionOtpExpiry(LocalDateTime.now().plusMinutes(CODE_TTL_MINUTES));
        user.setAccountActionType(normalized);
        userRepository.save(user);

        String verb = normalized.equals("DELETE") ? "delete" : "deactivate";
        emailSenderService.sendEmail(
                user.getEmail(),
                "Confirm account " + verb,
                "You requested to " + verb + " your account.\n\n"
                        + "Your confirmation code is: " + code + "\n"
                        + "It expires in " + CODE_TTL_MINUTES + " minutes.\n\n"
                        + "If you did not request this, ignore this email and "
                        + "consider changing your password.");

        return "A confirmation code has been sent to your email.";
    }

    @Override
    public String confirmAction(User user, String code) {
        if (code == null || code.isBlank())
            throw new RuntimeException("Confirmation code is required");
        if (user.getAccountActionOtp() == null || user.getAccountActionOtpExpiry() == null
                || user.getAccountActionType() == null)
            throw new RuntimeException("No account action was requested");
        if (LocalDateTime.now().isAfter(user.getAccountActionOtpExpiry()))
            throw new RuntimeException("Confirmation code expired, please request a new one");
        if (!code.trim().equals(user.getAccountActionOtp()))
            throw new RuntimeException("Invalid confirmation code");

        String action = user.getAccountActionType();

        user.setAccountActionOtp(null);
        user.setAccountActionOtpExpiry(null);
        user.setAccountActionType(null);

        if ("DEACTIVATE".equals(action)) {
            user.setStatus(UserStatus.DEACTIVATED);
            userRepository.save(user);
            return "Your account has been deactivated. Log in again any time to reactivate it.";
        }

        user.setStatus(UserStatus.DELETED);
        user.setDeletedAt(LocalDateTime.now());
        deactivateBrokerData(user);
        anonymize(user);
        userRepository.save(user);
        return "Your account has been permanently deleted.";
    }

    private void anonymize(User user) {
        Long id = user.getUserId();
        user.setFullName("Deleted User");

        user.setEmail("deleted+" + id + "@deleted.invalid");
        user.setUsernameField("deleted_" + id);
        user.setPhoneNumber(null);
        user.setEmailVerified(false);
        user.setPhoneVerified(false);

        user.setPasswordHash("DELETED_" + secureRandom.nextLong());
        user.setTwoFaEnabled(false);
        user.setTwoFaMethod(null);
        user.setTwoFaOtp(null);
        user.setTwoFaOtpExpiry(null);
        user.setOtp(null);
        user.setOtpExpiry(null);
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
    }

    private void deactivateBrokerData(User user) {
        if (user.getRole() == null
                || !"broker".equalsIgnoreCase(user.getRole().getName())) {
            return;
        }

        brokerRepository.findByUser_UserId(user.getUserId()).ifPresent(broker -> {
            policiesRepository.findByBroker_BrokerId(broker.getBrokerId())
                    .forEach(policy -> {
                        policy.setStatus("DEACTIVATED");
                        policiesRepository.save(policy);
                    });

            broker.setCompanyName("Deleted Broker");
            broker.setLicenseNumber("DELETED_" + broker.getBrokerId() + "_"
                    + System.currentTimeMillis());
            broker.setTaxId(null);
            broker.setWebsiteUrl(null);
            broker.setLogoUrl(null);
            broker.setIdFrontUrl(null);
            broker.setIdBackUrl(null);
            brokerRepository.save(broker);
        });
    }
}
