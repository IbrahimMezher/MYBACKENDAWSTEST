package com.flutterbackend.eveythingPolicies.coverage_tiers.domain;

import com.flutterbackend.eveythingPolicies.policies.domain.Policies;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "coverage_tiers")
public class CoverageTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tierId;

    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    private Policies policy;

    @Column(nullable = false)
    private String tierName;

    @Column(nullable = false)
    private BigDecimal coverageLimit;

    @Column(nullable = false)
    private BigDecimal premiumPrice;

    public CoverageTier() {}

    public Long getTierId() { return tierId; }
    public void setTierId(Long tierId) { this.tierId = tierId; }
    public Policies getPolicy() { return policy; }
    public void setPolicy(Policies policy) { this.policy = policy; }
    public String getTierName() { return tierName; }
    public void setTierName(String tierName) { this.tierName = tierName; }
    public BigDecimal getCoverageLimit() { return coverageLimit; }
    public void setCoverageLimit(BigDecimal coverageLimit) { this.coverageLimit = coverageLimit; }
    public BigDecimal getPremiumPrice() { return premiumPrice; }
    public void setPremiumPrice(BigDecimal premiumPrice) { this.premiumPrice = premiumPrice; }
}
