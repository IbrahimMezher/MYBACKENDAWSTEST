package com.flutterbackend.countries.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "countries")
public class Countries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long countryId;

    @Column(unique = true, nullable = false)
    private String countryName;

    @Column(nullable = false)
    private String currency;

    private String code;
    private String themeColor;
    private Double taxPercentage;

    @Column(name = "delivery_price")
    private Double deliveryPrice;
    private String regulations;

    public Countries() {}

    public Long getCountryId() { return countryId; }
    public void setCountryId(Long countryId) { this.countryId = countryId; }
    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getThemeColor() { return themeColor; }
    public void setThemeColor(String themeColor) { this.themeColor = themeColor; }
    public Double getTaxPercentage() { return taxPercentage; }
    public void setTaxPercentage(Double taxPercentage) { this.taxPercentage = taxPercentage; }
    public Double getDeliveryPrice() { return deliveryPrice; }
    public void setDeliveryPrice(Double deliveryPrice) { this.deliveryPrice = deliveryPrice; }
    public String getRegulations() { return regulations; }
    public void setRegulations(String regulations) { this.regulations = regulations; }
    public String getCode(){return code;}
    public void setCode(String code){this.code=code;}
}
