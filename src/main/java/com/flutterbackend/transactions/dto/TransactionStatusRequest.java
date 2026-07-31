package com.flutterbackend.transactions.dto;

import lombok.Data;

@Data
public class TransactionStatusRequest {
    private String paymentStatus;
    private String deliveryStatus;
}
