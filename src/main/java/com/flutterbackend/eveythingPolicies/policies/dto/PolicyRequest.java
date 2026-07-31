package com.flutterbackend.eveythingPolicies.policies.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PolicyRequest {
    public Long categoryId;
    public Long policyDurationId;
    public Long countryId;
    public BigDecimal deliveryPrice;
    public String policyName;
    public String description;
    public Integer waitingPeriodDays;
    public BigDecimal deductiblePerClaim;
    public BigDecimal deductiblePerYear;
    public Integer maxClaimsPerYear;
    public BigDecimal maxClaimAmount;
    public Integer minAge;
    public Integer maxAge;
    public Integer claimProcessingDays;
    public String status;
    public String documentUrl;
    public List<Long> exclusionTypeIds;
    public List<Long> benefitIds;
    public List<Long> inclusionIds;
    public List<Map<String, Object>> coverageTiers;
}
