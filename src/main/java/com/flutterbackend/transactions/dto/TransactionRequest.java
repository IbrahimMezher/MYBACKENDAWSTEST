package com.flutterbackend.transactions.dto;

import lombok.Data;

@Data
public class TransactionRequest {
    private Long policyId;
    private Long coverageTierId;
}
