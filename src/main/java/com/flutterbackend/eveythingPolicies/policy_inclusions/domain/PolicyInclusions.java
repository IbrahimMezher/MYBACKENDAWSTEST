package com.flutterbackend.eveythingPolicies.policy_inclusions.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flutterbackend.eveythingPolicies.policies.domain.Policies;
import com.flutterbackend.eveythingPolicies.policy_inclusions.domain.Inclusions;
import jakarta.persistence.*;

@Entity
@Table(name = "policy_inclusions")
public class PolicyInclusions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    @JsonIgnore
    private Policies policy;

    @ManyToOne
    @JoinColumn(name = "inclusion_id", nullable = false)
    private Inclusions inclusion;

    public Long getId() {
        return id;
    }

    public Policies getPolicy() {
        return policy;
    }

    public void setPolicy(Policies policy) {
        this.policy = policy;
    }

    public Inclusions getInclusion() {
        return inclusion;
    }

    public void setInclusion(Inclusions inclusion) {
        this.inclusion = inclusion;
    }
}
