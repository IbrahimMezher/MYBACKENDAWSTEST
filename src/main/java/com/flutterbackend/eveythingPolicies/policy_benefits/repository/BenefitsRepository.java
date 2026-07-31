package com.flutterbackend.eveythingPolicies.policy_benefits.repository;

import com.flutterbackend.eveythingPolicies.policy_benefits.domain.Benefits;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BenefitsRepository extends JpaRepository<Benefits, Long>
{
    void removeBenefitsById(Long benefitId);
}
