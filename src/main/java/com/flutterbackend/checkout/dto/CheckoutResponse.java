package com.flutterbackend.checkout.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CheckoutResponse {
    private String message;
    private int itemsPurchased;
    private BigDecimal totalAmountPaid;
    private List<Long> transactionIds;
}
