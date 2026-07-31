package com.flutterbackend.eveythingPolicies.policy_inclusions.web;

import com.flutterbackend.eveythingPolicies.policy_inclusions.domain.Inclusions;
import com.flutterbackend.eveythingPolicies.policy_inclusions.dto.InclusionRequest;
import com.flutterbackend.eveythingPolicies.policy_inclusions.service.InclusionsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inclusions")
public class InclusionsController {

    private final InclusionsService service;

    public InclusionsController(InclusionsService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    @PostMapping
    public Inclusions create(@RequestBody InclusionRequest body) {
        return service.create(body);
    }

    @GetMapping
    public List<Inclusions> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Inclusions getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    @PutMapping("/{id}")
    public Inclusions update(@PathVariable Long id, @RequestBody InclusionRequest body) {
        return service.update(id, body);
    }

    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
