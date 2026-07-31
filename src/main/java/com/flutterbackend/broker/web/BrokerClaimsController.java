package com.flutterbackend.broker.web;

import com.flutterbackend.claims.domain.Claims;
import com.flutterbackend.claims.dto.ClaimStatusRequest;
import com.flutterbackend.claims.service.ClaimsService;
import com.flutterbackend.user.domain.User;
import com.flutterbackend.util.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/broker/claims")
@CrossOrigin(origins = "*")
public class BrokerClaimsController {

    private final ClaimsService claimsService;
    private final CurrentUser currentUser;

    public BrokerClaimsController(ClaimsService claimsService, CurrentUser currentUser) {
        this.claimsService = claimsService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<Claims> getBrokerClaims(HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return claimsService.getByBrokerUserId(user.getUserId());
    }

    @PutMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestBody ClaimStatusRequest body, HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return claimsService.updateStatusForBroker(id, body, user.getUserId());
    }
}
