package com.flutterbackend.eveythingPolicies.policy_inclusions.repository;

import com.flutterbackend.eveythingPolicies.policy_inclusions.domain.Inclusions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InclusionsRepository extends JpaRepository<Inclusions, Long> {
}
