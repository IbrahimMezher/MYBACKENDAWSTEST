package com.flutterbackend.wishlist.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WishlistItemResponse {
    private Long wishlistItemId;
    private Long policyId;
    private String policyName;
    private String policyStatus;
    private String categoryName;
    private String description;
    private BigDecimal minPremiumPrice;
    private double avgRating;
    private long reviewCount;
    private String addedAt;
}
