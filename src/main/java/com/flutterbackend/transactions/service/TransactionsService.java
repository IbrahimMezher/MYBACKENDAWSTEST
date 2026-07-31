package com.flutterbackend.transactions.service;

import com.flutterbackend.auth.service.EmailSenderService;
import com.flutterbackend.eveythingPolicies.coverage_tiers.domain.CoverageTier;
import com.flutterbackend.eveythingPolicies.coverage_tiers.repository.CoverageTierRepository;
import com.flutterbackend.eveythingPolicies.policies.domain.Policies;
import com.flutterbackend.eveythingPolicies.policies.repository.PoliciesRepository;
import com.flutterbackend.notifications.service.NotificationService;
import com.flutterbackend.transactions.domain.DeliveryStatus;
import com.flutterbackend.transactions.domain.Transactions;
import com.flutterbackend.transactions.dto.TransactionRequest;
import com.flutterbackend.transactions.dto.TransactionStatusRequest;
import com.flutterbackend.transactions.repository.TransactionsRepository;
import com.flutterbackend.user.domain.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TransactionsService {

    private final TransactionsRepository transactionsRepository;
    private final PoliciesRepository policiesRepository;
    private final CoverageTierRepository coverageTierRepository;
    private final EmailSenderService emailSenderService;
    private final NotificationService notificationService;

    public TransactionsService(TransactionsRepository transactionsRepository,
                               PoliciesRepository policiesRepository,
                               CoverageTierRepository coverageTierRepository,
                               EmailSenderService emailSenderService,
                               NotificationService notificationService) {
        this.transactionsRepository = transactionsRepository;
        this.policiesRepository = policiesRepository;
        this.coverageTierRepository = coverageTierRepository;
        this.emailSenderService = emailSenderService;
        this.notificationService = notificationService;
    }

    public Transactions create(TransactionRequest body, User user) {
        Policies policy = policiesRepository.findById(body.getPolicyId())
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        CoverageTier tier = coverageTierRepository.findById(body.getCoverageTierId())
                .orElseThrow(() -> new RuntimeException("Coverage tier not found"));

        Transactions t = new Transactions();
        t.setUser(user);
        t.setPolicy(policy);
        t.setCoverageTier(tier);
        t.setAmountPaid(tier.getPremiumPrice());
        t.setPaymentStatus("PENDING");
        t.setPurchaseDate(LocalDateTime.now());
        t.setDeliveryStatus(DeliveryStatus.PENDING);
        t.setBrokerStatus("PENDING");
        return transactionsRepository.save(t);
    }

    public List<Transactions> getByUser(User user) {
        return transactionsRepository.findByUser_UserId(user.getUserId());
    }

    public List<Transactions> getByBroker(User user) {
        return transactionsRepository.findByPolicy_Broker_User_UserId(user.getUserId());
    }

    public String brokerDecision(Long transactionId, String decision, User brokerUser) {
        Transactions t = transactionsRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        Long transactionBrokerUserId = t.getPolicy().getBroker().getUser().getUserId();
        if (!transactionBrokerUserId.equals(brokerUser.getUserId()))
            throw new RuntimeException("Unauthorized: this transaction does not belong to your policies");

        if (!"PENDING".equals(t.getBrokerStatus()))
            throw new RuntimeException("This transaction has already been decided");

        String upperDecision = decision.toUpperCase();
        if (!upperDecision.equals("ACCEPTED") && !upperDecision.equals("REJECTED"))
            throw new RuntimeException("Decision must be ACCEPTED or REJECTED");

        t.setBrokerStatus(upperDecision);
        t.setBrokerDecisionDate(LocalDateTime.now());

        if ("ACCEPTED".equals(upperDecision)) {
            notificationService.create(
                    t.getUser(),
                    "Policy accepted",
                    "Your application for \"" + t.getPolicy().getPolicyName() + "\" was accepted by the broker.",
                    "POLICY");

            boolean isCod = "COD".equalsIgnoreCase(t.getPaymentMethod())
                    || "CASH".equalsIgnoreCase(t.getPaymentMethod())
                    || "CASH_ON_DELIVERY".equalsIgnoreCase(t.getPaymentMethod());
            t.setDeliveryStatus(isCod ? DeliveryStatus.ACCEPTED_BY_BROKER
                                      : DeliveryStatus.COMPLETED);
            t.setPaymentStatus("PENDING_PAYMENT");

            Integer waitingDays = t.getPolicy().getWaitingPeriodDays();
            int waiting = (waitingDays != null && waitingDays > 0) ? waitingDays : 0;

            try {
                emailSenderService.sendEmail(
                    t.getUser().getEmail(),
                    "Your Policy Request Has Been Accepted",
                    "Dear " + t.getUser().getFullName() + ",\n\n"
                    + "Good news! Your insurance policy \"" + t.getPolicy().getPolicyName() + "\" has been accepted by your broker.\n\n"
                    + "Your document will be delivered, and your coverage will activate once payment is received.\n"
                    + "Policy Duration: " + t.getPolicy().getPolicyDuration().getLabel() + "\n"
                    + "Waiting Period (after payment): " + waiting + " days\n\n"
                    + "I.A. Insurance Team"
                );
            } catch (Exception ignored) {}
        } else {
            t.setPaymentStatus("REJECTED");
            notificationService.create(
                    t.getUser(),
                    "Policy rejected",
                    "Your application for \"" + t.getPolicy().getPolicyName() + "\" was rejected by the broker.",
                    "POLICY");
            try {
                emailSenderService.sendEmail(
                    t.getUser().getEmail(),
                    "Your Policy Application Was Rejected",
                    "Dear " + t.getUser().getFullName() + ",\n\n"
                    + "We regret to inform you that your application for \"" + t.getPolicy().getPolicyName() + "\" has been rejected by the broker.\n\n"
                    + "Please contact support or browse other available policies.\n\n"
                    + "I.A. Insurance Team"
                );
            } catch (Exception ignored) {}
        }

        transactionsRepository.save(t);
        return "Transaction " + upperDecision.toLowerCase() + " successfully.";
    }

    public String updateStatus(Long id, TransactionStatusRequest body) {
        Transactions t = transactionsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (body.getPaymentStatus() != null) t.setPaymentStatus(body.getPaymentStatus());
        if (body.getDeliveryStatus() != null)
            t.setDeliveryStatus(DeliveryStatus.valueOf(body.getDeliveryStatus()));
        transactionsRepository.save(t);
        return "Transaction updated.";
    }

    public List<Transactions> getByUserId(Long userId) {
        return transactionsRepository.findByUser_UserId(userId);
    }

    public String updatePaymentStatus(Long id, String paymentStatus) {
        Transactions t = transactionsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        t.setPaymentStatus(paymentStatus);
        transactionsRepository.save(t);
        return "Status updated.";
    }

    public List<Transactions> getAll() {
        return transactionsRepository.findAll();
    }
}
