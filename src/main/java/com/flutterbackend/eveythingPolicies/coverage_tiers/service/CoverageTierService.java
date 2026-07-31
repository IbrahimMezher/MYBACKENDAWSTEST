package com.flutterbackend.eveythingPolicies.coverage_tiers.service;

import com.flutterbackend.eveythingPolicies.coverage_tiers.domain.CoverageTier;
import com.flutterbackend.eveythingPolicies.coverage_tiers.dto.CoverageTierRequest;
import com.flutterbackend.eveythingPolicies.coverage_tiers.repository.CoverageTierRepository;
import com.flutterbackend.eveythingPolicies.policies.domain.Policies;
import com.flutterbackend.eveythingPolicies.policies.repository.PoliciesRepository;
import org.springframework.http.ResponseEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class CoverageTierService {

    private final CoverageTierRepository coverageTierRepository;
    private final PoliciesRepository policiesRepository;

    public CoverageTierService(CoverageTierRepository coverageTierRepository,
                               PoliciesRepository policiesRepository) {
        this.coverageTierRepository = coverageTierRepository;
        this.policiesRepository = policiesRepository;
    }

    public List<CoverageTier> getAll() {
        return coverageTierRepository.findAll();
    }

    public ResponseEntity<CoverageTier> getById(Long id) {
        return coverageTierRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public List<CoverageTier> getByPolicy(Long policyId) {
        return coverageTierRepository.findByPolicy_PolicyId(policyId);
    }

    public CoverageTier create(CoverageTierRequest body) {
        Policies policy = policiesRepository.findById(body.getPolicyId())
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        CoverageTier tier = new CoverageTier();
        tier.setPolicy(policy);
        tier.setTierName(body.getTierName());
        tier.setCoverageLimit(body.getCoverageLimit());
        tier.setPremiumPrice(body.getPremiumPrice());
        return coverageTierRepository.save(tier);
    }

    public ResponseEntity<CoverageTier> update(Long id, CoverageTierRequest body) {
        return coverageTierRepository.findById(id).map(existing -> {
            existing.setTierName(body.getTierName());
            existing.setCoverageLimit(body.getCoverageLimit());
            existing.setPremiumPrice(body.getPremiumPrice());
            return ResponseEntity.ok(coverageTierRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<Void> delete(Long id) {
        if (!coverageTierRepository.existsById(id)) return ResponseEntity.notFound().build();
        coverageTierRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
