package com.flutterbackend.eveythingPolicies.policy_inclusions.service;

import com.flutterbackend.eveythingPolicies.policy_inclusions.domain.Inclusions;
import com.flutterbackend.eveythingPolicies.policy_inclusions.dto.InclusionRequest;
import com.flutterbackend.eveythingPolicies.policy_inclusions.repository.InclusionsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InclusionsService {

    private final InclusionsRepository repo;

    public InclusionsService(InclusionsRepository repo) {
        this.repo = repo;
    }

    public Inclusions create(InclusionRequest body) {
        Inclusions i = new Inclusions();
        i.setName(body.getName());
        i.setDescription(body.getDescription());
        return repo.save(i);
    }

    public List<Inclusions> getAll() {
        return repo.findAll();
    }

    public Inclusions getById(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public Inclusions update(Long id, InclusionRequest body) {
        Inclusions i = repo.findById(id).orElseThrow();
        if (body.getName() != null) i.setName(body.getName());
        if (body.getDescription() != null) i.setDescription(body.getDescription());
        return repo.save(i);
    }

    public String delete(Long id) {
        repo.deleteById(id);
        return "deleted";
    }
}
