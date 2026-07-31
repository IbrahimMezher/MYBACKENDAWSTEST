package com.flutterbackend.admin.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class AdminRegisterRequest {
    private String fullName;
    private String username;
    private String email;
    private String password;
    private String confirm;
    private String phoneNumber;

    @JsonAlias({"country_id", "countryId"})
    private Long countryId;

    @JsonAlias({"country_name", "countryName"})
    private String countryName;

    @JsonAlias({"country_code", "countryCode", "code"})
    private String countryCode;
}
