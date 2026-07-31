package com.flutterbackend.wishlist.repository;

import com.flutterbackend.wishlist.domain.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {

    List<WishlistItem> findByUser_UserId(Long userId);

    boolean existsByUser_UserIdAndPolicy_PolicyId(Long userId, Long policyId);

    Optional<WishlistItem> findByUser_UserIdAndPolicy_PolicyId(Long userId, Long policyId);

    void deleteByUser_UserIdAndPolicy_PolicyId(Long userId, Long policyId);
}
