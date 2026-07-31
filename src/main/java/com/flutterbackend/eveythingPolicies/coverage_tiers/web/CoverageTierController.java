package com.flutterbackend.eveythingPolicies.coverage_tiers.web;

import com.flutterbackend.eveythingPolicies.coverage_tiers.domain.CoverageTier;
import com.flutterbackend.eveythingPolicies.coverage_tiers.dto.CoverageTierRequest;
import com.flutterbackend.eveythingPolicies.coverage_tiers.service.CoverageTierService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coveragetiers")
@CrossOrigin(origins = "*")
public class CoverageTierController {

    private final CoverageTierService coverageTierService;

    public CoverageTierController(CoverageTierService coverageTierService) {
        this.coverageTierService = coverageTierService;
    }

    @GetMapping
    public List<CoverageTier> getAll() { return coverageTierService.getAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<CoverageTier> getById(@PathVariable Long id) {
        return coverageTierService.getById(id);
    }

    @GetMapping("/policy/{policyId}")
    public List<CoverageTier> getByPolicy(@PathVariable Long policyId) {
        return coverageTierService.getByPolicy(policyId);
    }

    @PreAuthorize("hasAnyRole('admin', 'superadmin','broker')")
    @PostMapping
    public CoverageTier create(@RequestBody CoverageTierRequest body) {
        return coverageTierService.create(body);
    }

    @PreAuthorize("hasAnyRole('admin', 'superadmin','broker')")
    @PutMapping("/{id}")
    public ResponseEntity<CoverageTier> update(@PathVariable Long id,
                                               @RequestBody CoverageTierRequest body) {
        return coverageTierService.update(id, body);
    }

    @PreAuthorize("hasAnyRole('admin', 'superadmin','broker')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return coverageTierService.delete(id);
    }
}
