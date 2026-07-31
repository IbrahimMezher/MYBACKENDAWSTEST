package com.flutterbackend.reviews.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BrokerReviewResponse {
    private Long reviewId;
    private Long policyId;
    private String policyName;
    private Integer rating;
    private String reviewText;
    private LocalDateTime createdAt;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
}
