package com.flutterbackend.offers.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OfferRequest {
    private Long brokerId;
    private Long policyId;
    private BigDecimal discountPercentage;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private String status;
}
