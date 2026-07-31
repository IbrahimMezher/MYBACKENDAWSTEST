package com.flutterbackend.eveythingPolicies.policy_inclusions.web;

import com.flutterbackend.eveythingPolicies.policy_inclusions.domain.PolicyInclusions;
import com.flutterbackend.eveythingPolicies.policy_inclusions.service.PolicyInclusionsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/policy-inclusions")
@CrossOrigin(origins = "*")
public class PolicyInclusionsController {

    private final PolicyInclusionsService service;

    public PolicyInclusionsController(PolicyInclusionsService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('admin', 'superadmin','broker')")
    @PostMapping("/{policyId}")
    public String add(@PathVariable Long policyId,
                      @RequestBody List<Long> inclusionIds) {
        return service.save(policyId, inclusionIds);
    }

    @GetMapping("/{policyId}")
    public List<PolicyInclusions> get(@PathVariable Long policyId) {
        return service.get(policyId);
    }
}
