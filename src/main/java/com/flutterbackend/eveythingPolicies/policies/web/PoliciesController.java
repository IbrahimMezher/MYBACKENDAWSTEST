package com.flutterbackend.eveythingPolicies.policies.web;

import com.flutterbackend.eveythingPolicies.policies.dto.PolicyRequest;
import com.flutterbackend.eveythingPolicies.policies.dto.PolicyResponse;
import com.flutterbackend.eveythingPolicies.policies.service.PoliciesService;
import com.flutterbackend.user.domain.User;
import com.flutterbackend.util.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/policies")
public class PoliciesController {

    private final PoliciesService policiesService;
    private final CurrentUser currentUser;

    public PoliciesController(PoliciesService policiesService,
                              CurrentUser currentUser) {
        this.policiesService = policiesService;
        this.currentUser = currentUser;
    }

    @PreAuthorize("hasAnyRole('admin', 'superadmin','broker')")
    @PostMapping("/create")
    public String createPolicy(@RequestBody PolicyRequest body,
                               HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return policiesService.create(body, user);
    }

    @PreAuthorize("hasAnyRole('admin', 'superadmin','broker')")
    @PutMapping("/broker/{id}")
    public String updatePolicy(@PathVariable Long id,
                               @RequestBody PolicyRequest body,
                               HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return policiesService.update(id, body, user);
    }

    @GetMapping("/allPolicies")
    public List<PolicyResponse> getAllPolicies(
            @RequestParam(value = "countryId", required = false) Long countryId) {
        return countryId != null
                ? policiesService.getAllByCountry(countryId)
                : policiesService.getAll();
    }

    @GetMapping("/{id}")
    public PolicyResponse getPolicyById(@PathVariable Long id) {
        return policiesService.getById(id);
    }

    @GetMapping("/broker")
    public List<PolicyResponse> brokerPolicies(HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return policiesService.getByBroker(user);
    }

    @GetMapping("/public/broker/{brokerId}")
    public List<PolicyResponse> activePoliciesByBroker(@PathVariable Long brokerId) {
        return policiesService.getActiveByBrokerId(brokerId);
    }
}
