package com.flutterbackend.broker.dto;

import lombok.Data;

@Data
public class BrokerSignupRequest {
    private String fullName;
    private String email;
    private String password;
    private String confirm;
    private Long countryId;
    private String companyName;
    private String licenseNumber;
    private String address;
    private String taxId;
    private String websiteUrl;
    private String logoUrl;
    private String idFrontUrl;
    private String idBackUrl;
    private String phoneNumber;
    private Boolean acceptedTerms;
}
