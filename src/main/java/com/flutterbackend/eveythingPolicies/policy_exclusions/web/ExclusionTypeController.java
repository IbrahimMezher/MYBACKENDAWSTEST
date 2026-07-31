package com.flutterbackend.eveythingPolicies.policy_exclusions.web;

import com.flutterbackend.eveythingPolicies.policy_exclusions.domain.ExclusionType;
import com.flutterbackend.eveythingPolicies.policy_exclusions.dto.ExclusionTypeRequest;
import com.flutterbackend.eveythingPolicies.policy_exclusions.service.ExclusionTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exclusiontypes")
@CrossOrigin(origins = "*")
public class ExclusionTypeController {

    private final ExclusionTypeService exclusionTypeService;

    public ExclusionTypeController(ExclusionTypeService exclusionTypeService) {
        this.exclusionTypeService = exclusionTypeService;
    }

    @GetMapping
    public List<ExclusionType> getAll() { return exclusionTypeService.getAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<ExclusionType> getById(@PathVariable Long id) {
        return exclusionTypeService.getById(id);
    }

    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    @PostMapping
    public ExclusionType create(@RequestBody ExclusionTypeRequest body) {
        return exclusionTypeService.create(body);
    }

    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    @PutMapping("/{id}")
    public ResponseEntity<ExclusionType> update(@PathVariable Long id,
                                                @RequestBody ExclusionTypeRequest body) {
        return exclusionTypeService.update(id, body);
    }

    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return exclusionTypeService.delete(id);
    }
}
