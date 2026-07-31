package com.flutterbackend.admin.web;

import com.flutterbackend.claims.dto.ClaimStatusRequest;
import com.flutterbackend.claims.domain.Claims;
import com.flutterbackend.claims.service.ClaimsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/claims")
@CrossOrigin(origins = "*")
public class AdminClaimsController {

    private final ClaimsService claimsService;

    public AdminClaimsController(ClaimsService claimsService) {
        this.claimsService = claimsService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public List<Claims> getAllClaims() {
        return claimsService.getAll();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public String updateStatus(@PathVariable Long id, @RequestBody ClaimStatusRequest body) {
        return claimsService.updateStatus(id, body);
    }
}
