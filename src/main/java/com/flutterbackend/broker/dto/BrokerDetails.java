package com.flutterbackend.broker.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BrokerDetails {
    private String companyName;
    private String licenseNumber;
    private String websiteUrl;
    private String logoUrl;
    private String idFrontUrl;
    private String idBackUrl;
    private LocalDateTime createdAt;
}
