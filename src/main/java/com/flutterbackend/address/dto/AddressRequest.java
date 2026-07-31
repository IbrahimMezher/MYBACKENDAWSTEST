package com.flutterbackend.address.dto;

import lombok.Data;

@Data
public class AddressRequest {
    private String fullName;
    private String phoneNumber;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private boolean isDefault;
}
