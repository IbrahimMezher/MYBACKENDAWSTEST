package com.flutterbackend.transactions.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flutterbackend.eveythingPolicies.coverage_tiers.domain.CoverageTier;
import com.flutterbackend.eveythingPolicies.policies.domain.Policies;
import com.flutterbackend.user.domain.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"transactions", "password"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    @JsonIgnoreProperties({"coverageTiers", "policyExclusions", "broker"})
    private Policies policy;

    @ManyToOne
    @JoinColumn(name = "coverage_tier_id", nullable = false)
    @JsonIgnoreProperties({"policy", "transactions"})
    private CoverageTier coverageTier;

    private LocalDateTime purchaseDate;

    @Column(nullable = false)
    private BigDecimal amountPaid;

    @Column(nullable = false)
    private String paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status")
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "delivery_price")
    private java.math.BigDecimal deliveryPrice;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "carrier_name")
    private String carrierName;

    @Column(name = "shipment_id")
    private String shipmentId;

    @Column(name = "tracking_url", length = 500)
    private String trackingUrl;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "broker_status")
    private String brokerStatus = "PENDING";

    @Column(name = "broker_decision_date")
    private LocalDateTime brokerDecisionDate;

    @Column(name = "policy_active_date")
    private LocalDateTime policyActiveDate;

    @Column(name = "claims_eligible_date")
    private LocalDateTime claimsEligibleDate;

    @Column(name = "field_values", columnDefinition = "TEXT")
    private String fieldValues;

    @Column(name = "notified_30days")
    private boolean notified30days = false;

    @Column(name = "notified_7days")
    private boolean notified7days = false;

    public Transactions() {}

    public Long getTransactionId()                         { return transactionId; }
    public void setTransactionId(Long transactionId)       { this.transactionId = transactionId; }
    public User getUser()                                  { return user; }
    public void setUser(User user)                         { this.user = user; }
    public Policies getPolicy()                            { return policy; }
    public void setPolicy(Policies policy)                 { this.policy = policy; }
    public CoverageTier getCoverageTier()                  { return coverageTier; }
    public void setCoverageTier(CoverageTier coverageTier) { this.coverageTier = coverageTier; }
    public LocalDateTime getPurchaseDate()                 { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate){ this.purchaseDate = purchaseDate; }
    public BigDecimal getAmountPaid()                      { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid)       { this.amountPaid = amountPaid; }
    public String getPaymentStatus()                       { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus)     { this.paymentStatus = paymentStatus; }
    public DeliveryStatus getDeliveryStatus()              { return deliveryStatus; }
    public void setDeliveryStatus(DeliveryStatus s)        { this.deliveryStatus = s; }
    public String getDeliveryAddress()                     { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public java.math.BigDecimal getDeliveryPrice()         { return deliveryPrice; }
    public void setDeliveryPrice(java.math.BigDecimal p)   { this.deliveryPrice = p; }
    public String getPaymentMethod()                       { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod)     { this.paymentMethod = paymentMethod; }
    public String getCarrierName()                         { return carrierName; }
    public void setCarrierName(String carrierName)         { this.carrierName = carrierName; }
    public String getShipmentId()                          { return shipmentId; }
    public void setShipmentId(String shipmentId)           { this.shipmentId = shipmentId; }
    public String getTrackingUrl()                         { return trackingUrl; }
    public void setTrackingUrl(String trackingUrl)         { this.trackingUrl = trackingUrl; }
    public LocalDateTime getShippedAt()                    { return shippedAt; }
    public void setShippedAt(LocalDateTime shippedAt)      { this.shippedAt = shippedAt; }
    public LocalDateTime getDeliveredAt()                  { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt)  { this.deliveredAt = deliveredAt; }
    public String getBrokerStatus()                        { return brokerStatus; }
    public void setBrokerStatus(String brokerStatus)       { this.brokerStatus = brokerStatus; }
    public LocalDateTime getBrokerDecisionDate()           { return brokerDecisionDate; }
    public void setBrokerDecisionDate(LocalDateTime t)     { this.brokerDecisionDate = t; }
    public LocalDateTime getPolicyActiveDate()             { return policyActiveDate; }
    public void setPolicyActiveDate(LocalDateTime t)       { this.policyActiveDate = t; }
    public LocalDateTime getClaimsEligibleDate()           { return claimsEligibleDate; }
    public void setClaimsEligibleDate(LocalDateTime t)     { this.claimsEligibleDate = t; }
    public boolean isNotified30days()                      { return notified30days; }
    public void setNotified30days(boolean notified30days)  { this.notified30days = notified30days; }
    public boolean isNotified7days()                       { return notified7days; }
    public void setNotified7days(boolean notified7days)    { this.notified7days = notified7days; }
    public String getFieldValues(){return fieldValues;}
    public void setFieldValues(String fieldValues){this.fieldValues = fieldValues; }
}
