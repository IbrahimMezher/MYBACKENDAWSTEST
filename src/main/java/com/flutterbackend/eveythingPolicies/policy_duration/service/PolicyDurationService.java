package com.flutterbackend.eveythingPolicies.policy_duration.service;

import com.flutterbackend.eveythingPolicies.policy_duration.domain.PolicyDuration;
import com.flutterbackend.eveythingPolicies.policy_duration.dto.PolicyDurationRequest;
import com.flutterbackend.eveythingPolicies.policy_duration.repository.PolicyDurationRepository;
import org.springframework.http.ResponseEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class PolicyDurationService {

    private final PolicyDurationRepository repo;

    public PolicyDurationService(PolicyDurationRepository repo) {
        this.repo = repo;
    }

    public List<PolicyDuration> getAll() { return repo.findAll(); }

    public ResponseEntity<PolicyDuration> getById(Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    public PolicyDuration create(PolicyDurationRequest body) {
        PolicyDuration pd = new PolicyDuration();
        pd.setLabel(body.label);
        pd.setDuration(body.duration);
        return repo.save(pd);
    }

    public ResponseEntity<PolicyDuration> update(Long id, PolicyDurationRequest body) {
        return repo.findById(id).map(existing -> {
            existing.setLabel(body.label);
            existing.setDuration(body.duration);
            return ResponseEntity.ok(repo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<Void> delete(Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
