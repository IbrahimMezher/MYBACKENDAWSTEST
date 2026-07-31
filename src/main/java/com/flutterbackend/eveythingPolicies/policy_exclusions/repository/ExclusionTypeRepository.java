package com.flutterbackend.eveythingPolicies.policy_exclusions.repository;

import com.flutterbackend.eveythingPolicies.policy_exclusions.domain.ExclusionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExclusionTypeRepository extends JpaRepository<ExclusionType, Long> {
}
