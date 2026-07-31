package com.flutterbackend.checkout.web;

import com.flutterbackend.checkout.dto.CheckoutRequest;
import com.flutterbackend.checkout.dto.CheckoutResponse;
import com.flutterbackend.checkout.service.CheckoutService;
import com.flutterbackend.user.domain.User;
import com.flutterbackend.util.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkout")
@CrossOrigin(origins = "*")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final CurrentUser currentUser;

    public CheckoutController(CheckoutService checkoutService, CurrentUser currentUser) {
        this.checkoutService = checkoutService;
        this.currentUser = currentUser;
    }

    @PostMapping
    @PreAuthorize("hasRole('customer')")
    public CheckoutResponse checkout(@RequestBody CheckoutRequest request,
                                     HttpServletRequest servletRequest) {
        User user = currentUser.getCurrentUser(servletRequest);
        return checkoutService.checkout(request, user);
    }
}
