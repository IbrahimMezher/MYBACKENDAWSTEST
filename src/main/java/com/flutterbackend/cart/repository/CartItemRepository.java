package com.flutterbackend.cart.repository;

import com.flutterbackend.cart.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser_UserId(Long userId);

    boolean existsByUser_UserIdAndPolicy_PolicyId(Long userId, Long policyId);

    long countByUser_UserId(Long userId);

    Optional<CartItem> findByCartItemIdAndUser_UserId(Long cartItemId, Long userId);

    void deleteByUser_UserId(Long userId);
}
