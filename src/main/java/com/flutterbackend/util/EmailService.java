package com.flutterbackend.util;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPolicyApprovalEmail(String toEmail, String customerName, String policyName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your Policy Has Been Approved");
        message.setText(
                "Dear " + customerName + ",\n\n" +
                        "Great news! Your insurance policy has been approved.\n\n" +
                        "Policy: " + policyName + "\n\n" +
                        "You can now view your active policy in the app.\n\n" +
                        "Best regards,\nI.A. Insurance Team"
        );
        mailSender.send(message);
    }

    public void sendPolicyRejectionEmail(String toEmail, String brokerName,
                                         String policyName, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Action needed: your policy was not approved");
        message.setText(
                "Dear " + brokerName + ",\n\n" +
                        "Your submitted policy was reviewed and could not be approved at this time.\n\n" +
                        "Policy: " + policyName + "\n" +
                        "Reason: " + (reason == null ? "Not specified" : reason) + "\n\n" +
                        "You can edit the policy in the app and resubmit it for review.\n\n" +
                        "Best regards,\nI.A. Insurance Team"
        );
        mailSender.send(message);
    }

    public void sendRenewalReminderEmail(String toEmail, String customerName,
                                         String policyName, int daysLeft) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your Policy Expires in " + daysLeft + " Days");
        message.setText(
                "Dear " + customerName + ",\n\n" +
                        "This is a reminder that your insurance policy is expiring soon.\n\n" +
                        "Policy: " + policyName + "\n" +
                        "Days remaining: " + daysLeft + " days\n\n" +
                        "Please renew your policy to avoid losing coverage.\n\n" +
                        "Best regards,\nI.A. Insurance Team"
        );
        mailSender.send(message);
    }
}
