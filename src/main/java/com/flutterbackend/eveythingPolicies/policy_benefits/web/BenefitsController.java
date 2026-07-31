package com.flutterbackend.eveythingPolicies.policy_benefits.web;

import com.flutterbackend.eveythingPolicies.policy_benefits.dto.BenefitRecieve;
import com.flutterbackend.eveythingPolicies.policy_benefits.dto.BenefitRequest;
import com.flutterbackend.eveythingPolicies.policy_benefits.service.BenefitsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/benefits")
public class BenefitsController {

    private final BenefitsService benefitsService;

    public BenefitsController(BenefitsService benefitsService) {
        this.benefitsService = benefitsService;
    }

    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    @PostMapping
    public String createBenefit(@RequestBody BenefitRequest request) {
        return benefitsService.createBenefit(request);
    }

    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    @PutMapping("/{id}")
    public String updateBenefit(@PathVariable Long id,
                                @RequestBody BenefitRequest request) {
        return benefitsService.updateBenefit(id, request);
    }

    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    @DeleteMapping("/{id}")
    public String deleteBenefit(@PathVariable Long id) {
        return benefitsService.deleteBenefit(id);
    }

    @GetMapping
    public List<BenefitRecieve> getAllBenefits() {
        return benefitsService.allBenefits();
    }
}
