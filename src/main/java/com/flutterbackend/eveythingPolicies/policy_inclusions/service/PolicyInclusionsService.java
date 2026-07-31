package com.flutterbackend.eveythingPolicies.policy_inclusions.service;

import com.flutterbackend.eveythingPolicies.policies.domain.Policies;
import com.flutterbackend.eveythingPolicies.policies.repository.PoliciesRepository;
import com.flutterbackend.eveythingPolicies.policy_inclusions.domain.PolicyInclusions;
import com.flutterbackend.eveythingPolicies.policy_inclusions.domain.Inclusions;
import com.flutterbackend.eveythingPolicies.policy_inclusions.repository.PolicyInclusionsRepository;
import com.flutterbackend.eveythingPolicies.policy_inclusions.repository.InclusionsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class PolicyInclusionsService {

    private final PolicyInclusionsRepository repository;
    private final PoliciesRepository policiesRepository;
    private final InclusionsRepository inclusionsRepository;

    public PolicyInclusionsService(
            PolicyInclusionsRepository repository,
            PoliciesRepository policiesRepository,
            InclusionsRepository inclusionsRepository) {
        this.repository = repository;
        this.policiesRepository = policiesRepository;
        this.inclusionsRepository = inclusionsRepository;
    }

    public String save(Long policyId, List<Long> inclusionIds) {

        Policies policy = policiesRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        repository.deleteByPolicy_PolicyId(policyId);

        for (Long id : inclusionIds) {

            Inclusions inclusion = inclusionsRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Inclusion not found"));

            PolicyInclusions pi = new PolicyInclusions();
            pi.setPolicy(policy);
            pi.setInclusion(inclusion);

            repository.save(pi);
        }

        return "Inclusions saved";
    }

    public List<PolicyInclusions> get(Long policyId) {
        return repository.findByPolicy_PolicyId(policyId);
    }
}
