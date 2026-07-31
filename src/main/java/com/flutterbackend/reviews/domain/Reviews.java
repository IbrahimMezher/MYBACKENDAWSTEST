package com.flutterbackend.reviews.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flutterbackend.eveythingPolicies.policies.domain.Policies;
import com.flutterbackend.user.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Reviews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"passwordHash","otp","otpExpiry","twoFaOtp","twoFaOtpExpiry","reviews","transactions","claims"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    @JsonIgnoreProperties({"broker","policyBenefits","policyInclusions","policyExclusions","coverageTiers","reviews","transactions"})
    private Policies policy;

    private Integer rating;
    private String reviewText;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Reviews() {}

    public Long getReviewId() { return reviewId; }
    public void setReviewId(Long reviewId) { this.reviewId = reviewId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Policies getPolicy() { return policy; }
    public void setPolicy(Policies policy) { this.policy = policy; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
