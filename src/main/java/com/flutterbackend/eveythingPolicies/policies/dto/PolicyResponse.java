package com.flutterbackend.eveythingPolicies.policies.dto;

import com.flutterbackend.broker.domain.Broker;
import com.flutterbackend.eveythingPolicies.coverage_tiers.domain.CoverageTier;
import com.flutterbackend.eveythingPolicies.policies.domain.Policies;
import com.flutterbackend.eveythingPolicies.policy_benefits.domain.PolicyBenefits;
import com.flutterbackend.eveythingPolicies.policy_duration.domain.PolicyDuration;
import com.flutterbackend.eveythingPolicies.policy_exclusions.domain.PolicyExclusion;
import com.flutterbackend.eveythingPolicies.policy_inclusions.domain.PolicyInclusions;
import com.flutterbackend.eveythingPolicies.policycategories.domain.PolicyCategories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PolicyResponse {

    private Long policyId;
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
    private String documentUrl;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Broker broker;
    private PolicyCategories category;
    private PolicyDuration policyDuration;
    private com.flutterbackend.countries.domain.Countries country;
    private BigDecimal deliveryPrice;
    private List<CoverageTier> coverageTiers;
    private List<PolicyExclusion> policyExclusions;
    private List<PolicyBenefits> policyBenefits;
    private List<PolicyInclusions> policyInclusions;

    private double avgRating;
    private long reviewCount;

    public PolicyResponse() {}

    public static PolicyResponse from(Policies p, double avgRating, long reviewCount) {
        PolicyResponse r = new PolicyResponse();
        r.policyId = p.getPolicyId();
        r.policyName = p.getPolicyName();
        r.description = p.getDescription();
        r.waitingPeriodDays = p.getWaitingPeriodDays();
        r.deductiblePerClaim = p.getDeductiblePerClaim();
        r.deductiblePerYear = p.getDeductiblePerYear();
        r.maxClaimsPerYear = p.getMaxClaimsPerYear();
        r.maxClaimAmount = p.getMaxClaimAmount();
        r.minAge = p.getMinAge();
        r.maxAge = p.getMaxAge();
        r.claimProcessingDays = p.getClaimProcessingDays();
        r.status = p.getStatus();
        r.documentUrl = p.getDocumentUrl();
        r.rejectionReason = p.getRejectionReason();
        r.createdAt = p.getCreatedAt();
        r.updatedAt = p.getUpdatedAt();
        r.broker = p.getBroker();
        r.category = p.getCategory();
        r.policyDuration = p.getPolicyDuration();
        r.country = p.getCountry();
        r.deliveryPrice = p.getDeliveryPrice();
        r.coverageTiers = p.getCoverageTiers();
        r.policyExclusions = p.getPolicyExclusions();
        r.policyBenefits = p.getPolicyBenefits();
        r.policyInclusions = p.getPolicyInclusions();
        r.avgRating = Math.round(avgRating * 10.0) / 10.0;
        r.reviewCount = reviewCount;
        return r;
    }

    public Long getPolicyId() { return policyId; }
    public String getPolicyName() { return policyName; }
    public String getDescription() { return description; }
    public Integer getWaitingPeriodDays() { return waitingPeriodDays; }
    public BigDecimal getDeductiblePerClaim() { return deductiblePerClaim; }
    public BigDecimal getDeductiblePerYear() { return deductiblePerYear; }
    public Integer getMaxClaimsPerYear() { return maxClaimsPerYear; }
    public BigDecimal getMaxClaimAmount() { return maxClaimAmount; }
    public Integer getMinAge() { return minAge; }
    public Integer getMaxAge() { return maxAge; }
    public Integer getClaimProcessingDays() { return claimProcessingDays; }
    public String getStatus() { return status; }
    public String getDocumentUrl() { return documentUrl; }
    public String getRejectionReason() { return rejectionReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Broker getBroker() { return broker; }
    public PolicyCategories getCategory() { return category; }
    public PolicyDuration getPolicyDuration() { return policyDuration; }
    public com.flutterbackend.countries.domain.Countries getCountry() { return country; }
    public BigDecimal getDeliveryPrice() { return deliveryPrice; }
    public List<CoverageTier> getCoverageTiers() { return coverageTiers; }
    public List<PolicyExclusion> getPolicyExclusions() { return policyExclusions; }
    public List<PolicyBenefits> getPolicyBenefits() { return policyBenefits; }
    public List<PolicyInclusions> getPolicyInclusions() { return policyInclusions; }
    public double getAvgRating() { return avgRating; }
    public long getReviewCount() { return reviewCount; }
}
