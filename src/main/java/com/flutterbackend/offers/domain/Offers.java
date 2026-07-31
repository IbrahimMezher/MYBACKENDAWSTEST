package com.flutterbackend.offers.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.flutterbackend.broker.domain.Broker;
import com.flutterbackend.eveythingPolicies.policies.domain.Policies;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "offers")
public class Offers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long offerId;

    @ManyToOne
    @JoinColumn(name = "broker_id", nullable = false)
    @JsonBackReference
    private Broker broker;

    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    @JsonBackReference
    private Policies policy;

    @Column(nullable = false)
    private BigDecimal discountPercentage;

    @Column(nullable = false)
    private LocalDate validFrom;

    @Column(nullable = false)
    private LocalDate validUntil;

    private String status = "Active";

    public Offers() {}

    public Long getOfferId() { return offerId; }
    public void setOfferId(Long offerId) { this.offerId = offerId; }
    public Broker getBroker() { return broker; }
    public void setBroker(Broker broker) { this.broker = broker; }
    public Policies getPolicy() { return policy; }
    public void setPolicy(Policies policy) { this.policy = policy; }
    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }
    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
