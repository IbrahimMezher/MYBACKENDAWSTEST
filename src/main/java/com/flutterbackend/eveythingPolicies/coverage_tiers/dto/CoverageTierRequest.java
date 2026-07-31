package com.flutterbackend.eveythingPolicies.coverage_tiers.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CoverageTierRequest {
    private Long policyId;
    private String tierName;
    private BigDecimal coverageLimit;
    private BigDecimal premiumPrice;
}
