package com.flutterbackend.cart.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse {
    private Long cartItemId;
    private Long policyId;
    private String policyName;
    private String policyStatus;
    private Long categoryId;
    private String categoryName;
    private Long coverageTierId;
    private String tierName;
    private BigDecimal premiumPrice;
    private BigDecimal coverageLimit;
    private BigDecimal deliveryPrice;
    private String addedAt;
}
