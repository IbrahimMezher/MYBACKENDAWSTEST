package com.flutterbackend.eveythingPolicies.policy_exclusions.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flutterbackend.eveythingPolicies.policies.domain.Policies;
import jakarta.persistence.*;

@Entity
@Table(name = "policy_exclusions")
public class PolicyExclusion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyExclusionId;

    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    @JsonIgnore
    private Policies policy;

    @ManyToOne
    @JoinColumn(name = "exclusion_type_id", nullable = false)
    private ExclusionType exclusionType;

    private String details;

    public PolicyExclusion() {}

    public Long getPolicyExclusionId() { return policyExclusionId; }
    public void setPolicyExclusionId(Long id) { this.policyExclusionId = id; }
    public Policies getPolicy() { return policy; }
    public void setPolicy(Policies policy) { this.policy = policy; }
    public ExclusionType getExclusionType() { return exclusionType; }
    public void setExclusionType(ExclusionType exclusionType) { this.exclusionType = exclusionType; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
