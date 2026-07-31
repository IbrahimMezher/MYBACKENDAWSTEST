package com.flutterbackend.eveythingPolicies.policy_inclusions.repository;

import com.flutterbackend.eveythingPolicies.policy_inclusions.domain.PolicyInclusions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyInclusionsRepository extends JpaRepository<PolicyInclusions, Long> {
    List<PolicyInclusions> findByPolicy_PolicyId(Long policyId);
    void deleteByPolicy_PolicyId(Long policyId);
}
