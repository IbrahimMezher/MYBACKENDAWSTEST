package com.flutterbackend.admin.web;

import com.flutterbackend.eveythingPolicies.policies.dto.PolicyResponse;
import com.flutterbackend.eveythingPolicies.policies.service.PoliciesService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/policies")
@CrossOrigin(origins = "*")
public class AdminPolicyController {

    private final PoliciesService policiesService;

    public AdminPolicyController(PoliciesService policiesService) {
        this.policiesService = policiesService;
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public List<PolicyResponse> pending() {
        return policiesService.getPendingApproval();
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public String approve(@PathVariable Long id) {
        return policiesService.approvePolicy(id);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public String reject(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : null;
        return policiesService.rejectPolicy(id, reason);
    }
}
