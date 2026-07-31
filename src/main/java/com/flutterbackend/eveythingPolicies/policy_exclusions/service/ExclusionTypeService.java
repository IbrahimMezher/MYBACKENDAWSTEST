package com.flutterbackend.eveythingPolicies.policy_exclusions.service;

import com.flutterbackend.eveythingPolicies.policy_exclusions.domain.ExclusionType;
import com.flutterbackend.eveythingPolicies.policy_exclusions.dto.ExclusionTypeRequest;
import com.flutterbackend.eveythingPolicies.policy_exclusions.repository.ExclusionTypeRepository;
import org.springframework.http.ResponseEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class ExclusionTypeService {

    private final ExclusionTypeRepository repo;

    public ExclusionTypeService(ExclusionTypeRepository repo) {
        this.repo = repo;
    }

    public List<ExclusionType> getAll() { return repo.findAll(); }

    public ResponseEntity<ExclusionType> getById(Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    public ExclusionType create(ExclusionTypeRequest body) {
        ExclusionType et = new ExclusionType();
        et.setName(body.name);
        et.setDescription(body.description);
        return repo.save(et);
    }

    public ResponseEntity<ExclusionType> update(Long id, ExclusionTypeRequest body) {
        return repo.findById(id).map(existing -> {
            existing.setName(body.name);
            existing.setDescription(body.description);
            return ResponseEntity.ok(repo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<Void> delete(Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
