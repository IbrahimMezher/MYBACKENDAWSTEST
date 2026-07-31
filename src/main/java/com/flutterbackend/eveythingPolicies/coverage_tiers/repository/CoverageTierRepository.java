package com.flutterbackend.eveythingPolicies.coverage_tiers.repository;

import com.flutterbackend.eveythingPolicies.coverage_tiers.domain.CoverageTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CoverageTierRepository extends JpaRepository<CoverageTier, Long> {
    List<CoverageTier> findByPolicy_PolicyId(Long policyId);

    void deleteByPolicy_PolicyId(Long policyId);
}
