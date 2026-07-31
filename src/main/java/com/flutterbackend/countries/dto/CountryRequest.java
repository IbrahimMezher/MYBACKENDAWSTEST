package com.flutterbackend.countries.dto;

import lombok.Data;

@Data
public class CountryRequest {
    private String countryName;
    private String currency;
    private String regulations;
    private String themeColor;
    private Double taxPercentage;
    private Double deliveryPrice;
    private String code;
}
