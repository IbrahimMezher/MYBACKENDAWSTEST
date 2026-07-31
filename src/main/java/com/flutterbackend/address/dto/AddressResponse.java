package com.flutterbackend.address.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {
    private Long addressId;
    private String fullName;
    private String phoneNumber;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private boolean isDefault;
}
