package com.flutterbackend.user.dto;

import com.flutterbackend.user.domain.UserStatus;

public class UserLoginResponse {
    public String accessToken;
    public String tokenType;
    public String role;
    public String fullName;
    public String email;
    public boolean email_verified;
    public boolean phone_verified;
    public UserStatus status;
    public Long countryId;
    public boolean twoFaEnabled;
    public String twoFaMethod;
    public boolean requiresTwoFa;

    public java.util.List<String> availableTwoFaMethods;
}
