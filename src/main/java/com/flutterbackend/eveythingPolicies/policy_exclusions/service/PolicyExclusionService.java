package com.flutterbackend.eveythingPolicies.policy_exclusions.service;

import com.flutterbackend.eveythingPolicies.policy_exclusions.domain.PolicyExclusion;
import com.flutterbackend.eveythingPolicies.policy_exclusions.repository.PolicyExclusionRepository;
import org.springframework.http.ResponseEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class PolicyExclusionService {

    private final PolicyExclusionRepository repo;

    public PolicyExclusionService(PolicyExclusionRepository repo) {
        this.repo = repo;
    }

    public List<PolicyExclusion> getAll() { return repo.findAll(); }

    public List<PolicyExclusion> getByPolicy(Long policyId) {
        return repo.findByPolicy_PolicyId(policyId);
    }

    public ResponseEntity<Void> delete(Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
