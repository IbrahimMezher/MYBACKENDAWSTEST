package com.flutterbackend.delivery.web;

import com.flutterbackend.delivery.service.DeliveryService;
import com.flutterbackend.user.domain.User;
import com.flutterbackend.util.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/delivery")
@CrossOrigin(origins = "*")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final CurrentUser currentUser;

    public DeliveryController(DeliveryService deliveryService, CurrentUser currentUser) {
        this.deliveryService = deliveryService;
        this.currentUser = currentUser;
    }

    @PutMapping("/{transactionId}/ship")
    @PreAuthorize("hasAnyRole('broker','admin','superadmin')")
    public String ship(@PathVariable Long transactionId,
                       @RequestBody Map<String, String> body,
                       HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return deliveryService.markShipped(
                transactionId,
                user,
                body.get("carrierName"),
                body.get("shipmentId"),
                body.get("trackingUrl"));
    }

    @PutMapping("/{transactionId}/paid")
    @PreAuthorize("hasAnyRole('broker','admin','superadmin')")
    public String markPaid(@PathVariable Long transactionId,
                           HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return deliveryService.markPaid(transactionId, user);
    }

    @PutMapping("/{transactionId}/not-received")
    @PreAuthorize("hasAnyRole('broker','admin','superadmin')")
    public String markNotReceived(@PathVariable Long transactionId,
                                  HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return deliveryService.markNotReceived(transactionId, user);
    }

    @PutMapping("/{transactionId}/delivered")
    @PreAuthorize("hasAnyRole('customer','broker','admin','superadmin')")
    public String delivered(@PathVariable Long transactionId,
                            HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return deliveryService.markDelivered(transactionId, user);
    }
}
