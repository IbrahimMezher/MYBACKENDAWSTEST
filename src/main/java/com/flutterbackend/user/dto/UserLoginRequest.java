package com.flutterbackend.user.dto;

import lombok.Data;

@Data
public class UserLoginRequest {
    public String email;
    public String password;
}
