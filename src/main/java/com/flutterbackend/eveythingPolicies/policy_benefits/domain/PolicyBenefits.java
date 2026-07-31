package com.flutterbackend.eveythingPolicies.policy_benefits.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flutterbackend.eveythingPolicies.policies.domain.Policies;
import jakarta.persistence.*;

@Entity
@Table(name = "policy_benefits")
public class PolicyBenefits {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    @JsonIgnore
    private Policies policy;

    @ManyToOne
    @JoinColumn(name = "benefit_id", nullable = false)
    private Benefits benefit;

    public Long getId() { return id; }

    public Policies getPolicy() { return policy; }
    public void setPolicy(Policies policy) { this.policy = policy; }

    public Benefits getBenefit() { return benefit; }
    public void setBenefit(Benefits benefit) { this.benefit = benefit; }
}
