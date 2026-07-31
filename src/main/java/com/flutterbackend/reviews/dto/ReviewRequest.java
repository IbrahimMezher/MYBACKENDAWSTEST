package com.flutterbackend.reviews.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long policyId;
    private Integer rating;
    private String reviewText;
}
