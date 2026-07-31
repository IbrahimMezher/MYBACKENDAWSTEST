package com.flutterbackend.cart.service;

import com.flutterbackend.cart.domain.CartItem;
import com.flutterbackend.cart.dto.AddToCartRequest;
import com.flutterbackend.cart.dto.CartItemResponse;
import com.flutterbackend.user.domain.User;

import java.util.List;

public interface CartService {

    String addItem(AddToCartRequest request, User user);

    List<CartItemResponse> getCart(User user);

    String removeItem(Long cartItemId, User user);

    void clearCart(User user);

    List<CartItem> getCartEntities(User user);
}
