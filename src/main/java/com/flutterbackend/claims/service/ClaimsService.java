package com.flutterbackend.claims.service;

import com.flutterbackend.claims.domain.Claims;
import com.flutterbackend.claims.dto.ClaimRequest;
import com.flutterbackend.claims.dto.ClaimStatusRequest;
import com.flutterbackend.claims.repository.ClaimsRepository;
import com.flutterbackend.notifications.service.NotificationService;
import com.flutterbackend.transactions.domain.Transactions;
import com.flutterbackend.transactions.repository.TransactionsRepository;
import com.flutterbackend.user.domain.User;
import com.flutterbackend.user.repository.UserRepository;
import com.flutterbackend.util.CurrentUser;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Service
public class ClaimsService {

    private final ClaimsRepository claimsRepository;
    private final TransactionsRepository transactionsRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;
    private final NotificationService notificationService;

    public ClaimsService(ClaimsRepository claimsRepository,
                         TransactionsRepository transactionsRepository,
                         UserRepository userRepository,
                         CurrentUser currentUser,
                         NotificationService notificationService) {
        this.claimsRepository = claimsRepository;
        this.transactionsRepository = transactionsRepository;
        this.userRepository = userRepository;
        this.currentUser = currentUser;
        this.notificationService = notificationService;
    }

    public String create(ClaimRequest body, User user) {
        Transactions transaction = transactionsRepository.findById(body.getTransactionId())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getUserId().equals(user.getUserId()))
            throw new RuntimeException("Unauthorized: this transaction does not belong to you");

        if (!"ACCEPTED".equals(transaction.getBrokerStatus()))
            throw new RuntimeException("You cannot file a claim until your policy has been accepted by the broker");

        LocalDateTime claimsEligibleDate = transaction.getClaimsEligibleDate();
        if (claimsEligibleDate == null || LocalDateTime.now().isBefore(claimsEligibleDate))
            throw new RuntimeException("You cannot file a claim yet. The waiting period has not passed. "
                + (claimsEligibleDate != null ? "Claims eligible from: " + claimsEligibleDate.toLocalDate() : ""));

        Claims claims = new Claims();
        claims.setTransaction(transaction);
        claims.setClaimStatus("PENDING");
        claims.setClaimDate(LocalDateTime.now());
        claims.setClaimAmount(body.getClaimAmount());
        claims.setRemarks(body.getRemarks());
        claimsRepository.save(claims);
        notificationService.create(
                user,
                "Claim submitted",
                "Your claim for \"" + transaction.getPolicy().getPolicyName() + "\" was submitted successfully.",
                "CLAIM");
        return "Claim submitted successfully.";
    }

    public List<Claims> getByUserId(User user) {
        return claimsRepository.findByTransaction_User_UserId(user.getUserId());
    }

    public List<Claims> getAll() {
        return claimsRepository.findAll();
    }

    public List<Claims> getByBrokerUserId(Long userId) {
        return claimsRepository.findByTransaction_Policy_Broker_User_UserId(userId);
    }

    public String updateStatusForBroker(Long id, ClaimStatusRequest body, Long brokerUserId) {
        Claims claim = claimsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        Long claimBrokerUserId = claim.getTransaction().getPolicy().getBroker().getUser().getUserId();
        if (!claimBrokerUserId.equals(brokerUserId))
            throw new RuntimeException("Unauthorized: claim does not belong to your policies");
        claim.setClaimStatus(body.getClaimStatus());
        claimsRepository.save(claim);
        notificationService.create(
                claim.getTransaction().getUser(),
                "Claim status updated",
                "Your claim for \"" + claim.getTransaction().getPolicy().getPolicyName()
                        + "\" is now " + body.getClaimStatus() + ".",
                "CLAIM");
        return "Claim status updated to " + body.getClaimStatus();
    }

    public String updateStatus(Long id, ClaimStatusRequest body) {
        Claims claims = claimsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        claims.setClaimStatus(body.getClaimStatus());
        claimsRepository.save(claims);
        notificationService.create(
                claims.getTransaction().getUser(),
                "Claim status updated",
                "Your claim for \"" + claims.getTransaction().getPolicy().getPolicyName()
                        + "\" is now " + body.getClaimStatus() + ".",
                "CLAIM");
        return "Claim status updated.";
    }
}
