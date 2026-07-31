package com.flutterbackend.wishlist.service;

import com.flutterbackend.user.domain.User;
import com.flutterbackend.wishlist.dto.WishlistItemResponse;

import java.util.List;

public interface WishlistService {

    boolean add(Long policyId, User user);

    boolean remove(Long policyId, User user);

    boolean toggle(Long policyId, User user);

    List<WishlistItemResponse> getWishlist(User user);

    List<Long> getWishlistedPolicyIds(User user);
}
