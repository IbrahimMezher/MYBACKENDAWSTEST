package com.flutterbackend.wishlist.service.impl;

import com.flutterbackend.eveythingPolicies.coverage_tiers.domain.CoverageTier;
import com.flutterbackend.eveythingPolicies.policies.domain.Policies;
import com.flutterbackend.eveythingPolicies.policies.repository.PoliciesRepository;
import com.flutterbackend.reviews.repository.ReviewsRepository;
import com.flutterbackend.user.domain.User;
import com.flutterbackend.wishlist.domain.WishlistItem;
import com.flutterbackend.wishlist.dto.WishlistItemResponse;
import com.flutterbackend.wishlist.repository.WishlistRepository;
import com.flutterbackend.wishlist.service.WishlistService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final PoliciesRepository policiesRepository;
    private final ReviewsRepository reviewsRepository;

    public WishlistServiceImpl(WishlistRepository wishlistRepository,
                               PoliciesRepository policiesRepository,
                               ReviewsRepository reviewsRepository) {
        this.wishlistRepository = wishlistRepository;
        this.policiesRepository = policiesRepository;
        this.reviewsRepository = reviewsRepository;
    }

    @Override
    public boolean add(Long policyId, User user) {
        if (wishlistRepository.existsByUser_UserIdAndPolicy_PolicyId(user.getUserId(), policyId)) {
            return true;
        }
        Policies policy = policiesRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        WishlistItem item = new WishlistItem();
        item.setUser(user);
        item.setPolicy(policy);
        wishlistRepository.save(item);
        return true;
    }

    @Override
    public boolean remove(Long policyId, User user) {
        wishlistRepository.deleteByUser_UserIdAndPolicy_PolicyId(user.getUserId(), policyId);
        return false;
    }

    @Override
    public boolean toggle(Long policyId, User user) {
        boolean exists = wishlistRepository
                .existsByUser_UserIdAndPolicy_PolicyId(user.getUserId(), policyId);
        if (exists) {
            return remove(policyId, user);
        }
        return add(policyId, user);
    }

    @Override
    public List<WishlistItemResponse> getWishlist(User user) {
        return wishlistRepository.findByUser_UserId(user.getUserId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<Long> getWishlistedPolicyIds(User user) {
        return wishlistRepository.findByUser_UserId(user.getUserId()).stream()
                .map(w -> w.getPolicy().getPolicyId())
                .toList();
    }

    private WishlistItemResponse toResponse(WishlistItem w) {
        Policies p = w.getPolicy();
        BigDecimal minPrice = null;
        if (p.getCoverageTiers() != null) {
            for (CoverageTier t : p.getCoverageTiers()) {
                if (t.getPremiumPrice() == null) continue;
                if (minPrice == null || t.getPremiumPrice().compareTo(minPrice) < 0) {
                    minPrice = t.getPremiumPrice();
                }
            }
        }
        double avg = reviewsRepository.averageRatingByPolicy(p.getPolicyId());
        long count = reviewsRepository.countByPolicy_PolicyId(p.getPolicyId());
        return WishlistItemResponse.builder()
                .wishlistItemId(w.getWishlistItemId())
                .policyId(p.getPolicyId())
                .policyName(p.getPolicyName())
                .policyStatus(p.getStatus())
                .categoryName(p.getCategory() != null ? p.getCategory().getCategoryName() : null)
                .description(p.getDescription())
                .minPremiumPrice(minPrice)
                .avgRating(Math.round(avg * 10.0) / 10.0)
                .reviewCount(count)
                .addedAt(w.getAddedAt() != null ? w.getAddedAt().toString() : null)
                .build();
    }
}
