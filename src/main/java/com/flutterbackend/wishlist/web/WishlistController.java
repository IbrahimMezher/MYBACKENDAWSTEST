package com.flutterbackend.wishlist.web;

import com.flutterbackend.user.domain.User;
import com.flutterbackend.util.CurrentUser;
import com.flutterbackend.wishlist.dto.WishlistItemResponse;
import com.flutterbackend.wishlist.dto.WishlistRequest;
import com.flutterbackend.wishlist.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wishlist")
@CrossOrigin(origins = "*")
public class WishlistController {

    private final WishlistService wishlistService;
    private final CurrentUser currentUser;

    public WishlistController(WishlistService wishlistService, CurrentUser currentUser) {
        this.wishlistService = wishlistService;
        this.currentUser = currentUser;
    }

    @GetMapping
    @PreAuthorize("hasRole('customer')")
    public List<WishlistItemResponse> getWishlist(HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return wishlistService.getWishlist(user);
    }

    @GetMapping("/ids")
    @PreAuthorize("hasRole('customer')")
    public List<Long> getWishlistedIds(HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return wishlistService.getWishlistedPolicyIds(user);
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasRole('customer')")
    public Map<String, Object> toggle(@RequestBody WishlistRequest body,
                                      HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        boolean wishlisted = wishlistService.toggle(body.getPolicyId(), user);
        return Map.of("policyId", body.getPolicyId(), "wishlisted", wishlisted);
    }

    @DeleteMapping("/{policyId}")
    @PreAuthorize("hasRole('customer')")
    public Map<String, Object> remove(@PathVariable Long policyId,
                                      HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        wishlistService.remove(policyId, user);
        return Map.of("policyId", policyId, "wishlisted", false);
    }
}
