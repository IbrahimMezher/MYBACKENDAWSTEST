package com.flutterbackend.payments.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long transactionId;
    private String paymentMethod;
}
