package com.flutterbackend.user.dto;

import lombok.Data;

@Data
public class UserSignupRequest {
    public String fullName;
    public String email;
    public String password;
    public String confirm;
    public String phoneNumber;
    public Long countryId;
    public Boolean acceptedTerms;
}
