package com.flutterbackend.delivery.service;

import com.flutterbackend.transactions.domain.DeliveryStatus;
import com.flutterbackend.transactions.domain.Transactions;
import com.flutterbackend.transactions.repository.TransactionsRepository;
import com.flutterbackend.auth.service.EmailSenderService;
import com.flutterbackend.user.domain.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class DeliveryService {

    private final TransactionsRepository transactionsRepository;
    private final EmailSenderService emailSenderService;

    public DeliveryService(TransactionsRepository transactionsRepository,
                           EmailSenderService emailSenderService) {
        this.transactionsRepository = transactionsRepository;
        this.emailSenderService = emailSenderService;
    }

    public String markShipped(Long transactionId, User brokerUser,
                              String carrierName, String shipmentId, String trackingUrl) {
        Transactions t = loadOwnedByBroker(transactionId, brokerUser);

        if (shipmentId == null || shipmentId.isBlank())
            throw new RuntimeException("Shipment ID is required");
        if (trackingUrl == null || trackingUrl.isBlank())
            throw new RuntimeException("Tracking URL is required");
        if (!isValidUrl(trackingUrl))
            throw new RuntimeException("Tracking URL must start with http:// or https://");

        if (t.getDeliveryStatus() != DeliveryStatus.ACCEPTED_BY_BROKER
                && t.getDeliveryStatus() != DeliveryStatus.SHIPPED)
            throw new RuntimeException("Order must be accepted before it can be shipped");

        t.setCarrierName(carrierName != null && !carrierName.isBlank() ? carrierName.trim() : null);
        t.setShipmentId(shipmentId.trim());
        t.setTrackingUrl(trackingUrl.trim());
        t.setShippedAt(LocalDateTime.now());
        t.setDeliveryStatus(DeliveryStatus.SHIPPED);
        transactionsRepository.save(t);

        try {
            emailSenderService.sendEmail(
                    t.getUser().getEmail(),
                    "Your policy document has shipped",
                    "Dear " + t.getUser().getFullName() + ",\n\n"
                            + "Your policy document is on its way.\n\n"
                            + (carrierName != null ? "Carrier: " + carrierName + "\n" : "")
                            + "Shipment ID: " + shipmentId + "\n"
                            + "Track it here: " + trackingUrl + "\n\n"
                            + "I.A. Insurance Team");
        } catch (Exception ignored) {}

        return "Order marked as shipped.";
    }

    public String markDelivered(Long transactionId, User actingUser) {
        Transactions t = transactionsRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        boolean isCustomer = t.getUser() != null
                && t.getUser().getUserId().equals(actingUser.getUserId());
        boolean isOwningBroker = t.getPolicy() != null
                && t.getPolicy().getBroker() != null
                && t.getPolicy().getBroker().getUser() != null
                && t.getPolicy().getBroker().getUser().getUserId().equals(actingUser.getUserId());
        if (!isCustomer && !isOwningBroker)
            throw new RuntimeException("Not authorized for this order");

        if (t.getDeliveryStatus() != DeliveryStatus.SHIPPED)
            throw new RuntimeException("Order must be shipped before it can be marked delivered");

        t.setDeliveryStatus(DeliveryStatus.DELIVERED);
        t.setDeliveredAt(LocalDateTime.now());
        transactionsRepository.save(t);
        return "Order marked as delivered.";
    }

    public String markPaid(Long transactionId, User brokerUser) {
        Transactions t = loadOwnedByBroker(transactionId, brokerUser);

        if (!"ACCEPTED".equalsIgnoreCase(t.getBrokerStatus()))
            throw new RuntimeException("Only accepted orders can be marked paid");
        if (t.getDeliveryStatus() == DeliveryStatus.NOT_RECEIVED)
            throw new RuntimeException("This order is marked not received. Resolve the delivery issue before marking it paid.");
        if ("PAID".equalsIgnoreCase(t.getPaymentStatus()))
            return "Order is already marked paid.";

        LocalDateTime paidAt = LocalDateTime.now();

        Integer waitingDays = t.getPolicy().getWaitingPeriodDays();
        int waiting = (waitingDays != null && waitingDays > 0) ? waitingDays : 0;
        LocalDateTime activeDate = paidAt.plusDays(waiting);
        t.setPolicyActiveDate(activeDate);

        Integer claimProcessingDays = t.getPolicy().getClaimProcessingDays();
        int claimWait = (claimProcessingDays != null && claimProcessingDays > 0)
                ? claimProcessingDays : 0;
        t.setClaimsEligibleDate(activeDate.plusDays(claimWait));

        t.setPaymentStatus("PAID");
        transactionsRepository.save(t);

        try {
            emailSenderService.sendEmail(
                    t.getUser().getEmail(),
                    "Payment received — your policy is now active",
                    "Dear " + t.getUser().getFullName() + ",\n\n"
                            + "We've received payment for your policy \""
                            + t.getPolicy().getPolicyName() + "\".\n\n"
                            + "Coverage active from: " + activeDate.toLocalDate() + "\n"
                            + "Claims eligible from: " + t.getClaimsEligibleDate().toLocalDate() + "\n\n"
                            + "I.A. Insurance Team");
        } catch (Exception ignored) {}

        return "Payment confirmed. Policy will activate after the waiting period.";
    }

    public String markNotReceived(Long transactionId, User brokerUser) {
        Transactions t = loadOwnedByBroker(transactionId, brokerUser);

        if (!"ACCEPTED".equalsIgnoreCase(t.getBrokerStatus()))
            throw new RuntimeException("Only accepted orders can be marked not received");
        if ("PAID".equalsIgnoreCase(t.getPaymentStatus()))
            throw new RuntimeException("Paid orders cannot be marked not received");
        if (t.getDeliveryStatus() != DeliveryStatus.SHIPPED)
            throw new RuntimeException("Only shipped orders can be marked not received");

        t.setDeliveryStatus(DeliveryStatus.NOT_RECEIVED);
        t.setPaymentStatus("DELIVERY_ISSUE");
        transactionsRepository.save(t);

        try {
            emailSenderService.sendEmail(
                    t.getUser().getEmail(),
                    "Delivery issue recorded",
                    "Dear " + t.getUser().getFullName() + ",\n\n"
                            + "Your broker marked the delivery for policy \""
                            + t.getPolicy().getPolicyName()
                            + "\" as not received. Payment has not been confirmed and your policy has not been activated yet.\n\n"
                            + "Please contact your broker to resolve the delivery issue.\n\n"
                            + "I.A. Insurance Team");
        } catch (Exception ignored) {}

        return "Order marked as not received. Payment remains pending.";
    }

    private Transactions loadOwnedByBroker(Long transactionId, User brokerUser) {
        Transactions t = transactionsRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        boolean owns = t.getPolicy() != null
                && t.getPolicy().getBroker() != null
                && t.getPolicy().getBroker().getUser() != null
                && t.getPolicy().getBroker().getUser().getUserId().equals(brokerUser.getUserId());
        if (!owns) throw new RuntimeException("Not authorized for this order");
        return t;
    }

    private boolean isValidUrl(String url) {
        String u = url.trim().toLowerCase();
        return u.startsWith("http://") || u.startsWith("https://");
    }

}
