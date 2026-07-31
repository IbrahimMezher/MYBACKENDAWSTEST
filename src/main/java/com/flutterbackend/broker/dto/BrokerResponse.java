package com.flutterbackend.broker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrokerResponse {
    private Long brokerId;
    private Long userId;
    private String fullName;
    private String companyName;
    private String licenseNumber;
    private String logoUrl;
    private long activePolicyCount;
}
