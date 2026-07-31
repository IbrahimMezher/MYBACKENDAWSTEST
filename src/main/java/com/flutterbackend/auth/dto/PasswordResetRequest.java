package com.flutterbackend.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PasswordResetRequest {

    @JsonProperty("Password")
    private String Password;

    @JsonProperty("NewPassword")
    private String NewPassword;

    @JsonProperty("Confirm")
    private String Confirm;
}
