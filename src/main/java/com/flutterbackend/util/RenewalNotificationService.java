package com.flutterbackend.util;

import com.flutterbackend.auth.service.EmailSenderService;
import com.flutterbackend.transactions.domain.Transactions;
import com.flutterbackend.transactions.repository.TransactionsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RenewalNotificationService {

    private final TransactionsRepository transactionsRepository;
    private final EmailSenderService emailSenderService;

    public RenewalNotificationService(TransactionsRepository transactionsRepository,
                                      EmailSenderService emailSenderService) {
        this.transactionsRepository = transactionsRepository;
        this.emailSenderService = emailSenderService;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void sendRenewalNotifications() {
        List<Transactions> allTransactions = transactionsRepository.findAll();

        for (Transactions t : allTransactions) {
            try {
                if (!"ACCEPTED".equals(t.getBrokerStatus())) continue;
                if (t.getPolicyActiveDate() == null) continue;
                if (t.getPolicy().getPolicyDuration() == null) continue;

                String duration = t.getPolicy().getPolicyDuration().getDuration();
                int months = getDurationMonths(duration);

                LocalDateTime expiryDate = t.getPolicyActiveDate().plusMonths(months);
                LocalDateTime now = LocalDateTime.now();
                long daysLeft = java.time.Duration.between(now, expiryDate).toDays();

                String customerEmail = t.getUser().getEmail();
                String customerName = t.getUser().getFullName();
                String policyName = t.getPolicy().getPolicyName();

                if (daysLeft >= 29 && daysLeft <= 30 && !t.isNotified30days()) {
                    emailSenderService.sendEmail(
                            customerEmail,
                            "Your Policy Expires in 30 Days - " + policyName,
                            "Dear " + customerName + ",\n\n"
                                    + "Your insurance policy \"" + policyName
                                    + "\" will expire in 30 days on "
                                    + expiryDate.toLocalDate() + ".\n\n"
                                    + "Please contact your broker to renew.\n\n"
                                    + "I.A. Insurance"
                    );
                    t.setNotified30days(true);
                    transactionsRepository.save(t);
                }

                if (daysLeft >= 6 && daysLeft <= 7 && !t.isNotified7days()) {
                    emailSenderService.sendEmail(
                            customerEmail,
                            "URGENT: Your Policy Expires in 7 Days - " + policyName,
                            "Dear " + customerName + ",\n\n"
                                    + "Your insurance policy \"" + policyName
                                    + "\" will expire in 7 days on "
                                    + expiryDate.toLocalDate() + ".\n\n"
                                    + "Please contact your broker immediately to renew.\n\n"
                                    + "I.A. Insurance"
                    );
                    t.setNotified7days(true);
                    transactionsRepository.save(t);
                }

            } catch (Exception e) {
                System.err.println("Renewal notification error: " + e.getMessage());
            }
        }
    }

    private int getDurationMonths(String duration) {
        if (duration == null) return 12;
        switch (duration.toUpperCase()) {
            case "MONTHLY": return 1;
            case "QUARTERLY": return 3;
            case "SEMI_ANNUAL": return 6;
            case "TWO_YEARS": return 24;
            case "THREE_YEARS": return 36;
            default: return 12;
        }
    }
}
