package com.flutterbackend.eveythingPolicies.policies.repository;

import com.flutterbackend.eveythingPolicies.policies.domain.Policies;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PoliciesRepository extends JpaRepository<Policies, Long> {
    List<Policies> findByBroker_BrokerId(Long brokerId);
    List<Policies> findByStatus(String status);
}
