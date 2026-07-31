package com.flutterbackend.cart.dto;

import lombok.Data;

@Data
public class AddToCartRequest {
    private Long policyId;
    private Long coverageTierId;
}
