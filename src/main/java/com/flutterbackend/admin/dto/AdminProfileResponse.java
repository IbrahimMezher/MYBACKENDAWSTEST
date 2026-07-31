package com.flutterbackend.admin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminProfileResponse {
    private Long adminId;
    private String fullName;
    private String username;
    private String email;
    private String role;
}
