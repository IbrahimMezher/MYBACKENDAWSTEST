package com.flutterbackend.eveythingPolicies.policy_exclusions.repository;

import com.flutterbackend.eveythingPolicies.policy_exclusions.domain.PolicyExclusion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyExclusionRepository extends JpaRepository<PolicyExclusion, Long> {
    List<PolicyExclusion> findByPolicy_PolicyId(Long policyId);
    void deleteByPolicy_PolicyId(Long policyId);
}
