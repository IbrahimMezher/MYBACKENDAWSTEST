package com.flutterbackend.eveythingPolicies.policy_benefits.repository;

import com.flutterbackend.eveythingPolicies.policy_benefits.domain.PolicyBenefits;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyBenefitsRepository extends JpaRepository<PolicyBenefits, Long> {
    List<PolicyBenefits> findByPolicy_PolicyId(Long policyId);
    void deleteByPolicy_PolicyId(Long policyId);
}
