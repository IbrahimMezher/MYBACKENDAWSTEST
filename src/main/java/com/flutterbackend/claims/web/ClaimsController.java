package com.flutterbackend.claims.web;

import com.flutterbackend.claims.domain.Claims;
import com.flutterbackend.claims.dto.ClaimRequest;
import com.flutterbackend.claims.dto.ClaimStatusRequest;
import com.flutterbackend.claims.service.ClaimsService;
import com.flutterbackend.user.domain.User;
import com.flutterbackend.util.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/claims")
@CrossOrigin(origins = "*")
public class ClaimsController {

    private final ClaimsService claimsService;
    private final CurrentUser currentUser;

    public ClaimsController(ClaimsService claimsService, CurrentUser currentUser) {
        this.claimsService = claimsService;
        this.currentUser = currentUser;
    }

    @PostMapping("/create")
    public String createClaim(@RequestBody ClaimRequest body, HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return claimsService.create(body, user);
    }

    @GetMapping("/user/me")
    public List<Claims> getUserClaims(HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return claimsService.getByUserId(user);
    }

    @PutMapping("/{id}/status")
    public String updateClaimStatus(@PathVariable Long id, @RequestBody ClaimStatusRequest body) {
        return claimsService.updateStatus(id, body);
    }
}
