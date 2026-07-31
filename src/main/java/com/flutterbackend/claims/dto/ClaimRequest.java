package com.flutterbackend.claims.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClaimRequest {
    private Long transactionId;
    private BigDecimal claimAmount;
    private String remarks;
}
