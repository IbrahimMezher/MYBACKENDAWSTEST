package com.flutterbackend.eveythingPolicies.policies.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flutterbackend.broker.domain.Broker;
import com.flutterbackend.eveythingPolicies.coverage_tiers.domain.CoverageTier;
import com.flutterbackend.eveythingPolicies.policy_benefits.domain.PolicyBenefits;
import com.flutterbackend.eveythingPolicies.policy_duration.domain.PolicyDuration;
import com.flutterbackend.eveythingPolicies.policy_inclusions.domain.PolicyInclusions;
import com.flutterbackend.eveythingPolicies.policycategories.domain.PolicyCategories;
import com.flutterbackend.eveythingPolicies.policy_exclusions.domain.PolicyExclusion;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "policies")
public class Policies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyId;

    @ManyToOne
    @JoinColumn(name = "broker_id", nullable = false)
    @JsonIgnoreProperties({"policies", "user"})
    private Broker broker;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnoreProperties({"policies"})
    private PolicyCategories category;

    @ManyToOne
    @JoinColumn(name = "policy_duration_id", nullable = false)
    private PolicyDuration policyDuration;

    @ManyToOne
    @JoinColumn(name = "country_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private com.flutterbackend.countries.domain.Countries country;

    @Column(name = "delivery_price")
    private BigDecimal deliveryPrice;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("policy")
    private List<CoverageTier> coverageTiers;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("policy")
    private List<PolicyExclusion> policyExclusions;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("policy")
    private List<PolicyBenefits> policyBenefits;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("policy")
    private List<PolicyInclusions> policyInclusions;

    @Column(nullable = false)
    private String policyName;
    private String description;
    private Integer waitingPeriodDays;
    private BigDecimal deductiblePerClaim;
    private BigDecimal deductiblePerYear;
    private Integer maxClaimsPerYear;
    private BigDecimal maxClaimAmount;
    private Integer minAge;
    private Integer maxAge;
    private Integer claimProcessingDays;
    private String status;

    @Column(name = "document_url", length = 500)
    private String documentUrl;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { this.createdAt = this.updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public Policies() {}

    public Long getPolicyId() { return policyId; }
    public void setPolicyId(Long policyId) { this.policyId = policyId; }
    public Broker getBroker() { return broker; }
    public void setBroker(Broker broker) { this.broker = broker; }
    public PolicyCategories getCategory() { return category; }
    public void setCategory(PolicyCategories category) { this.category = category; }
    public PolicyDuration getPolicyDuration() { return policyDuration; }
    public void setPolicyDuration(PolicyDuration policyDuration) { this.policyDuration = policyDuration; }
    public com.flutterbackend.countries.domain.Countries getCountry() { return country; }
    public void setCountry(com.flutterbackend.countries.domain.Countries country) { this.country = country; }
    public BigDecimal getDeliveryPrice() { return deliveryPrice; }
    public void setDeliveryPrice(BigDecimal deliveryPrice) { this.deliveryPrice = deliveryPrice; }
    public List<CoverageTier> getCoverageTiers() { return coverageTiers; }
    public void setCoverageTiers(List<CoverageTier> coverageTiers) { this.coverageTiers = coverageTiers; }
    public List<PolicyExclusion> getPolicyExclusions() { return policyExclusions; }
    public void setPolicyExclusions(List<PolicyExclusion> policyExclusions) { this.policyExclusions = policyExclusions; }
    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getWaitingPeriodDays() { return waitingPeriodDays; }
    public void setWaitingPeriodDays(Integer waitingPeriodDays) { this.waitingPeriodDays = waitingPeriodDays; }
    public BigDecimal getDeductiblePerClaim() { return deductiblePerClaim; }
    public void setDeductiblePerClaim(BigDecimal deductiblePerClaim) { this.deductiblePerClaim = deductiblePerClaim; }
    public BigDecimal getDeductiblePerYear() { return deductiblePerYear; }
    public void setDeductiblePerYear(BigDecimal deductiblePerYear) { this.deductiblePerYear = deductiblePerYear; }
    public Integer getMaxClaimsPerYear() { return maxClaimsPerYear; }
    public void setMaxClaimsPerYear(Integer maxClaimsPerYear) { this.maxClaimsPerYear = maxClaimsPerYear; }
    public BigDecimal getMaxClaimAmount() { return maxClaimAmount; }
    public void setMaxClaimAmount(BigDecimal maxClaimAmount) { this.maxClaimAmount = maxClaimAmount; }
    public Integer getMinAge() { return minAge; }
    public void setMinAge(Integer minAge) { this.minAge = minAge; }
    public Integer getMaxAge() { return maxAge; }
    public void setMaxAge(Integer maxAge) { this.maxAge = maxAge; }
    public Integer getClaimProcessingDays() { return claimProcessingDays; }
    public void setClaimProcessingDays(Integer claimProcessingDays) { this.claimProcessingDays = claimProcessingDays; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<PolicyBenefits> getPolicyBenefits() { return policyBenefits; }
    public void setPolicyBenefits(List<PolicyBenefits> policyBenefits) { this.policyBenefits = policyBenefits; }
    public List<PolicyInclusions> getPolicyInclusions() { return policyInclusions; }
    public void setPolicyInclusions(List<PolicyInclusions> policyInclusions) { this.policyInclusions = policyInclusions; }

}
