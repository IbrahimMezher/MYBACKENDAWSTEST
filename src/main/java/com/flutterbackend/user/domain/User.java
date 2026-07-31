package com.flutterbackend.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flutterbackend.countries.domain.Countries;
import com.flutterbackend.role.domain.Role;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_email",  columnNames = "email"),
        @UniqueConstraint(name = "uk_users_phone",  columnNames = "phone_number")
    },
    indexes = {
        @Index(name = "idx_users_email", columnList = "email"),
        @Index(name = "idx_users_phone", columnList = "phone_number")
    }
)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = true, unique = false)
    private String phoneNumber;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "phone_verified", nullable = false)
    private boolean phoneVerified = false;

    @JsonIgnore
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Countries country;

    @Column(name = "otp")
    private String otp;

    @Column(name = "otp_expiry")
    private LocalDateTime otpExpiry;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    @Column(name = "twofa_enabled", nullable = false)
    private boolean twoFaEnabled = false;

    @Column(name = "twofa_method")
    private String twoFaMethod;

    @Column(name = "twofa_otp")
    private String twoFaOtp;

    @Column(name = "twofa_otp_expiry")
    private LocalDateTime twoFaOtpExpiry;

    @Column(name = "privacy_profile_visible", nullable = false)
    private boolean privacyProfileVisible = true;

    @Column(name = "privacy_contact_visible", nullable = false)
    private boolean privacyContactVisible = true;

    @Column(name = "account_action_otp")
    private String accountActionOtp;

    @Column(name = "account_action_otp_expiry")
    private LocalDateTime accountActionOtpExpiry;

    @Column(name = "account_action_type")
    private String accountActionType;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() { this.createdAt = this.updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getName()));
    }

    @JsonIgnore
    @Override public String getPassword()              { return passwordHash; }
    @Override public String getUsername()              { return email; }
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }

    @Override public boolean isEnabled()               { return status == UserStatus.ACTIVE; }

    @JsonIgnore
    public String getAccountActionOtp()                { return accountActionOtp; }
    public void setAccountActionOtp(String v)          { this.accountActionOtp = v; }
    @JsonIgnore
    public LocalDateTime getAccountActionOtpExpiry()   { return accountActionOtpExpiry; }
    public void setAccountActionOtpExpiry(LocalDateTime v) { this.accountActionOtpExpiry = v; }
    public String getAccountActionType()               { return accountActionType; }
    public void setAccountActionType(String v)         { this.accountActionType = v; }
    public LocalDateTime getDeletedAt()                { return deletedAt; }
    public void setDeletedAt(LocalDateTime v)          { this.deletedAt = v; }

    public User() {}

    public Long getUserId()                            { return userId; }
    public void setUserId(Long userId)                 { this.userId = userId; }
    public String getFullName()                        { return fullName; }
    public void setFullName(String fullName)           { this.fullName = fullName; }
    public String getEmail()                           { return email; }
    public void setEmail(String email)                 { this.email = email; }
    public String getPhoneNumber()                     { return phoneNumber; }
    public void setPhoneNumber(String p)               { this.phoneNumber = p; }
    public boolean isEmailVerified()                   { return emailVerified; }
    public void setEmailVerified(boolean v)            { this.emailVerified = v; }
    public boolean isPhoneVerified()                   { return phoneVerified; }
    public void setPhoneVerified(boolean v)            { this.phoneVerified = v; }
    @JsonIgnore
    public String getPasswordHash()                    { return passwordHash; }
    public void setPasswordHash(String h)              { this.passwordHash = h; }
    public Role getRole()                              { return role; }
    public void setRole(Role role)                     { this.role = role; }
    public Countries getCountry()                      { return country; }
    public void setCountry(Countries c)                { this.country = c; }
    @JsonIgnore
    public String getOtp()                             { return otp; }
    public void setOtp(String otp)                     { this.otp = otp; }
    @JsonIgnore
    public LocalDateTime getOtpExpiry()                { return otpExpiry; }
    public void setOtpExpiry(LocalDateTime t)          { this.otpExpiry = t; }
    public LocalDateTime getCreatedAt()                { return createdAt; }
    public LocalDateTime getUpdatedAt()                { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t)          { this.updatedAt = t; }
    public UserStatus getStatus()                      { return status; }
    public void setStatus(UserStatus status)           { this.status = status; }
    @JsonIgnore
    public String getResetToken()                      { return resetToken; }
    public void setResetToken(String resetToken)       { this.resetToken = resetToken; }
    @JsonIgnore
    public LocalDateTime getResetTokenExpiry()         { return resetTokenExpiry; }
    public void setResetTokenExpiry(LocalDateTime t)   { this.resetTokenExpiry = t; }
    public String getUsernameField()                   { return username; }
    public void setUsernameField(String username)      { this.username = username; }
    public boolean isTwoFaEnabled()                    { return twoFaEnabled; }
    public void setTwoFaEnabled(boolean twoFaEnabled)  { this.twoFaEnabled = twoFaEnabled; }
    public String getTwoFaMethod()                     { return twoFaMethod; }
    public void setTwoFaMethod(String twoFaMethod)     { this.twoFaMethod = twoFaMethod; }
    @JsonIgnore
    public String getTwoFaOtp()                        { return twoFaOtp; }
    public void setTwoFaOtp(String twoFaOtp)           { this.twoFaOtp = twoFaOtp; }
    @JsonIgnore
    public LocalDateTime getTwoFaOtpExpiry()           { return twoFaOtpExpiry; }
    public void setTwoFaOtpExpiry(LocalDateTime t)     { this.twoFaOtpExpiry = t; }
    public boolean isPrivacyProfileVisible()           { return privacyProfileVisible; }
    public void setPrivacyProfileVisible(boolean v)    { this.privacyProfileVisible = v; }
    public boolean isPrivacyContactVisible()           { return privacyContactVisible; }
    public void setPrivacyContactVisible(boolean v)    { this.privacyContactVisible = v; }
}
