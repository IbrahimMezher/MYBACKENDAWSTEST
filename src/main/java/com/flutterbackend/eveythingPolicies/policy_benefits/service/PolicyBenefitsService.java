package com.flutterbackend.eveythingPolicies.policy_benefits.service;

import com.flutterbackend.eveythingPolicies.policies.domain.Policies;
import com.flutterbackend.eveythingPolicies.policies.repository.PoliciesRepository;
import com.flutterbackend.eveythingPolicies.policy_benefits.domain.Benefits;
import com.flutterbackend.eveythingPolicies.policy_benefits.domain.PolicyBenefits;
import com.flutterbackend.eveythingPolicies.policy_benefits.repository.BenefitsRepository;
import com.flutterbackend.eveythingPolicies.policy_benefits.repository.PolicyBenefitsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class PolicyBenefitsService {

    private final PolicyBenefitsRepository repository;
    private final PoliciesRepository policiesRepository;
    private final BenefitsRepository benefitsRepository;

    public PolicyBenefitsService(PolicyBenefitsRepository repository,
                                 PoliciesRepository policiesRepository,
                                 BenefitsRepository benefitsRepository) {
        this.repository = repository;
        this.policiesRepository = policiesRepository;
        this.benefitsRepository = benefitsRepository;
    }

    public String save(Long policyId,List<Long> benefitIds) {
        Policies policy = policiesRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        repository.deleteByPolicy_PolicyId(policyId);
        for (Long benefitId : benefitIds) {

            Benefits benefit = benefitsRepository.findById(benefitId)
                    .orElseThrow(() -> new RuntimeException("Benefit not found"));

            PolicyBenefits pb = new PolicyBenefits();
            pb.setPolicy(policy);
            pb.setBenefit(benefit);

            repository.save(pb);
        }
        return "Benefits saved";
    }

    public List<PolicyBenefits> get(Long policyId) {
        return repository.findByPolicy_PolicyId(policyId);
    }
}
