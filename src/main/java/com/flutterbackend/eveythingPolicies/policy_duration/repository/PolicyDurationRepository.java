package com.flutterbackend.eveythingPolicies.policy_duration.repository;

import com.flutterbackend.eveythingPolicies.policy_duration.domain.PolicyDuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyDurationRepository extends JpaRepository<PolicyDuration, Long> {
}
