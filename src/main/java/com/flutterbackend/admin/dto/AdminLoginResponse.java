package com.flutterbackend.admin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminLoginResponse {
    private String accessToken;
    private String tokenType;
    private String role;
    private String fullName;
    private String email;
    private boolean requiresTwoFa;
    private boolean phone_verified;
    private java.util.List<String> availableTwoFaMethods;
}
