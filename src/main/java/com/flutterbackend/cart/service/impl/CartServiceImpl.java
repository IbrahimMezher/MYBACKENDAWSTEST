package com.flutterbackend.cart.service.impl;

import com.flutterbackend.cart.domain.CartItem;
import com.flutterbackend.cart.dto.AddToCartRequest;
import com.flutterbackend.cart.dto.CartItemResponse;
import com.flutterbackend.cart.repository.CartItemRepository;
import com.flutterbackend.cart.service.CartService;
import com.flutterbackend.eveythingPolicies.coverage_tiers.domain.CoverageTier;
import com.flutterbackend.eveythingPolicies.coverage_tiers.repository.CoverageTierRepository;
import com.flutterbackend.eveythingPolicies.policies.domain.Policies;
import com.flutterbackend.eveythingPolicies.policies.repository.PoliciesRepository;
import com.flutterbackend.user.domain.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final PoliciesRepository policiesRepository;
    private final CoverageTierRepository coverageTierRepository;

    public CartServiceImpl(CartItemRepository cartItemRepository,
                           PoliciesRepository policiesRepository,
                           CoverageTierRepository coverageTierRepository) {
        this.cartItemRepository = cartItemRepository;
        this.policiesRepository = policiesRepository;
        this.coverageTierRepository = coverageTierRepository;
    }

    @Override
    public String addItem(AddToCartRequest request, User user) {

        Policies policy = policiesRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        if (!"ACTIVE".equalsIgnoreCase(policy.getStatus())) {
            throw new RuntimeException("Policy is not available for purchase");
        }

        CoverageTier tier = coverageTierRepository.findById(request.getCoverageTierId())
                .orElseThrow(() -> new RuntimeException("Coverage tier not found"));

        if (!tier.getPolicy().getPolicyId().equals(policy.getPolicyId())) {
            throw new RuntimeException("Coverage tier does not belong to this policy");
        }

        if (cartItemRepository.existsByUser_UserIdAndPolicy_PolicyId(
                user.getUserId(), policy.getPolicyId())) {
            throw new RuntimeException("This policy is already in your cart");
        }

        long currentCartSize = cartItemRepository.countByUser_UserId(user.getUserId());
        if (currentCartSize > 0) {
            throw new RuntimeException("You can only buy one policy at a time. Please remove the item in your cart first.");
        }

        CartItem item = new CartItem();
        item.setUser(user);
        item.setPolicy(policy);
        item.setCoverageTier(tier);

        cartItemRepository.save(item);
        return "Policy added to cart successfully.";
    }

    @Override
    public List<CartItemResponse> getCart(User user) {
        return cartItemRepository.findByUser_UserId(user.getUserId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public String removeItem(Long cartItemId, User user) {
        CartItem item = cartItemRepository
                .findByCartItemIdAndUser_UserId(cartItemId, user.getUserId())
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cartItemRepository.delete(item);
        return "Item removed from cart.";
    }

    @Override
    public void clearCart(User user) {
        cartItemRepository.deleteByUser_UserId(user.getUserId());
    }

    @Override
    public List<CartItem> getCartEntities(User user) {
        return cartItemRepository.findByUser_UserId(user.getUserId());
    }

    private CartItemResponse toResponse(CartItem item) {

        Long catId = null;
        String catName = "";
        try {
            catId = item.getPolicy().getCategory().getCategoryId();
            catName = item.getPolicy().getCategory().getCategoryName();
        } catch (Exception ignored) {}

        return CartItemResponse.builder()
                .cartItemId(item.getCartItemId())
                .policyId(item.getPolicy().getPolicyId())
                .policyName(item.getPolicy().getPolicyName())
                .policyStatus(item.getPolicy().getStatus())
                .categoryId(catId)
                .categoryName(catName)
                .coverageTierId(item.getCoverageTier().getTierId())
                .tierName(item.getCoverageTier().getTierName())
                .premiumPrice(item.getCoverageTier().getPremiumPrice())
                .coverageLimit(item.getCoverageTier().getCoverageLimit())
                .deliveryPrice(item.getPolicy().getDeliveryPrice())
                .addedAt(item.getAddedAt().toString())
                .build();
    }
}
