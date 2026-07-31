package com.flutterbackend.broker.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flutterbackend.user.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "brokers")
public class Broker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long brokerId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonBackReference
    @JsonIgnoreProperties({"broker", "password", "transactions"})
    private User user;

    @Column(nullable = false)
    private String companyName;

    @Column(unique = true, nullable = false)
    private String licenseNumber;

    private String address;
    private String taxId;
    private String websiteUrl;
    private String logoUrl;
    private String idFrontUrl;
    private String idBackUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { this.createdAt = this.updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public Broker() {}

    public Long getBrokerId()                     { return brokerId; }
    public void setBrokerId(Long brokerId)        { this.brokerId = brokerId; }
    public User getUser()                         { return user; }
    public void setUser(User user)                { this.user = user; }
    public String getCompanyName()                { return companyName; }
    public void setCompanyName(String c)          { this.companyName = c; }
    public String getLicenseNumber()              { return licenseNumber; }
    public void setLicenseNumber(String l)        { this.licenseNumber = l; }
    public String getAddress()                    { return address; }
    public void setAddress(String a)              { this.address = a; }
    public String getTaxId()                      { return taxId; }
    public void setTaxId(String t)                { this.taxId = t; }
    public String getWebsiteUrl()                 { return websiteUrl; }
    public void setWebsiteUrl(String w)           { this.websiteUrl = w; }
    public String getLogoUrl()                    { return logoUrl; }
    public void setLogoUrl(String l)              { this.logoUrl = l; }
    public String getIdFrontUrl()                 { return idFrontUrl; }
    public void setIdFrontUrl(String idFrontUrl)  { this.idFrontUrl = idFrontUrl; }
    public String getIdBackUrl()                  { return idBackUrl; }
    public void setIdBackUrl(String idBackUrl)    { this.idBackUrl = idBackUrl; }
    public LocalDateTime getCreatedAt()           { return createdAt; }
    public LocalDateTime getUpdatedAt()           { return updatedAt; }
}
