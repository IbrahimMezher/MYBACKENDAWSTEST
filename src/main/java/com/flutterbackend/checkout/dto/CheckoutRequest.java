package com.flutterbackend.checkout.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private Long addressId;

    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String deliveryPhoneNumber;

    private String paymentMethod;
    private String fieldValues;
}
