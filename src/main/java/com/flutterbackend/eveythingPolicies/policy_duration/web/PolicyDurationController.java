package com.flutterbackend.eveythingPolicies.policy_duration.web;

import com.flutterbackend.eveythingPolicies.policy_duration.domain.PolicyDuration;
import com.flutterbackend.eveythingPolicies.policy_duration.dto.PolicyDurationRequest;
import com.flutterbackend.eveythingPolicies.policy_duration.service.PolicyDurationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/policyduration")
@CrossOrigin(origins = "*")
public class PolicyDurationController {

    private final PolicyDurationService policyDurationService;

    public PolicyDurationController(PolicyDurationService policyDurationService) {
        this.policyDurationService = policyDurationService;
    }

    @GetMapping
    public List<PolicyDuration> getAll() { return policyDurationService.getAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<PolicyDuration> getById(@PathVariable Long id) {
        return policyDurationService.getById(id);
    }

    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    @PostMapping
    public PolicyDuration create(@RequestBody PolicyDurationRequest body) {
        return policyDurationService.create(body);
    }

    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    @PutMapping("/{id}")
    public ResponseEntity<PolicyDuration> update(@PathVariable Long id,
                                                 @RequestBody PolicyDurationRequest body) {
        return policyDurationService.update(id, body);
    }

    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return policyDurationService.delete(id);
    }
}
