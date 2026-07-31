package com.flutterbackend.cart.web;

import com.flutterbackend.cart.dto.AddToCartRequest;
import com.flutterbackend.cart.dto.CartItemResponse;
import com.flutterbackend.cart.service.CartService;
import com.flutterbackend.user.domain.User;
import com.flutterbackend.util.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;
    private final CurrentUser currentUser;

    public CartController(CartService cartService, CurrentUser currentUser) {
        this.cartService = cartService;
        this.currentUser = currentUser;
    }

    @GetMapping
    @PreAuthorize("hasRole('customer')")
    public List<CartItemResponse> getCart(HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return cartService.getCart(user);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('customer')")
    public String addItem(@RequestBody AddToCartRequest body,
                          HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return cartService.addItem(body, user);
    }

    @DeleteMapping("/{cartItemId}")
    @PreAuthorize("hasRole('customer')")
    public String removeItem(@PathVariable Long cartItemId,
                             HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return cartService.removeItem(cartItemId, user);
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('customer')")
    public String clearCart(HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        cartService.clearCart(user);
        return "Cart cleared.";
    }
}
