package com.flutterbackend.eveythingPolicies.policy_benefits.web;

import com.flutterbackend.eveythingPolicies.policy_benefits.domain.PolicyBenefits;
import com.flutterbackend.eveythingPolicies.policy_benefits.service.PolicyBenefitsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/policy-benefits")
@CrossOrigin(origins = "*")
public class PolicyBenefitsController {

    private final PolicyBenefitsService policyBenefitsService;

    public PolicyBenefitsController(PolicyBenefitsService policyBenefitsService) {
        this.policyBenefitsService = policyBenefitsService;
    }

    @PreAuthorize("hasAnyRole('admin', 'superadmin','broker')")
    @PostMapping("/{policyId}")
    public String addBenefits(@PathVariable Long policyId,
                              @RequestBody List<Long> benefitIds) {
        return policyBenefitsService.save(policyId, benefitIds);
    }

    @GetMapping("/{policyId}")
    public List<PolicyBenefits> get(@PathVariable Long policyId) {
        return policyBenefitsService.get(policyId);
    }
}
