package com.flutterbackend.eveythingPolicies.policy_exclusions.web;

import com.flutterbackend.eveythingPolicies.policy_exclusions.domain.PolicyExclusion;
import com.flutterbackend.eveythingPolicies.policy_exclusions.service.PolicyExclusionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/policyexclusions")
@CrossOrigin(origins = "*")
public class PolicyExclusionController {

    private final PolicyExclusionService policyExclusionService;

    public PolicyExclusionController(PolicyExclusionService policyExclusionService) {
        this.policyExclusionService = policyExclusionService;
    }

    @GetMapping
    public List<PolicyExclusion> getAll() { return policyExclusionService.getAll(); }

    @GetMapping("/policy/{policyId}")
    public List<PolicyExclusion> getByPolicy(@PathVariable Long policyId) {
        return policyExclusionService.getByPolicy(policyId);
    }

    @PreAuthorize("hasAnyRole('admin', 'superadmin','broker')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return policyExclusionService.delete(id);
    }
}
