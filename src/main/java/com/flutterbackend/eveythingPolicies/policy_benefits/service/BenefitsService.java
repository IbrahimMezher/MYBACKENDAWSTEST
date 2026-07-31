package com.flutterbackend.eveythingPolicies.policy_benefits.service;

import com.flutterbackend.eveythingPolicies.policy_benefits.domain.Benefits;
import com.flutterbackend.eveythingPolicies.policy_benefits.dto.BenefitRecieve;
import com.flutterbackend.eveythingPolicies.policy_benefits.dto.BenefitRequest;
import com.flutterbackend.eveythingPolicies.policy_benefits.repository.BenefitsRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BenefitsService {

    private final BenefitsRepository benefitsRepository;

    public BenefitsService(BenefitsRepository benefitsRepository) {
        this.benefitsRepository = benefitsRepository;
    }

    public String createBenefit(BenefitRequest body) {

        Benefits benefit = new Benefits();

        benefit.setTitle(body.getTitle());
        benefit.setDescription(body.getDescription());

        benefitsRepository.save(benefit);

        return "Benefit created successfully";
    }

    public String deleteBenefit(Long benefitId) {

        benefitsRepository.deleteById(benefitId);

        return "Benefit deleted successfully";
    }

    public String updateBenefit(Long benefitId, BenefitRequest body) {

        Benefits benefit = benefitsRepository.findById(benefitId)
                .orElseThrow(() -> new RuntimeException("Benefit not found"));

        if (body.getTitle() != null) {
            benefit.setTitle(body.getTitle());
        }

        if (body.getDescription() != null) {
            benefit.setDescription(body.getDescription());
        }

        benefitsRepository.save(benefit);

        return "Benefit updated successfully";
    }

    public List<BenefitRecieve> allBenefits() {

        return benefitsRepository.findAll()
                .stream()
                .map(b -> {
                    BenefitRecieve dto = new BenefitRecieve();
                    dto.setId(b.getId());
                    dto.setTitle(b.getTitle());
                    dto.setDescription(b.getDescription());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
