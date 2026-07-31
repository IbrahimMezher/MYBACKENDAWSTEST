package com.flutterbackend.eveythingPolicies.policy_duration.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "policy_duration")
public class PolicyDuration
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyDurationId;

    @Column(nullable = false)
    private String label;

    @Column(unique = true, nullable = false)
    private String duration;

    public PolicyDuration(){}

    public long getPolicyDurationId(){return policyDurationId;}
    public void setPolicyDurationId(long policyDurationId){this.policyDurationId = policyDurationId;}

    public String getLabel() {return label;}
    public void setLabel(String label) {this.label = label;}

    public String getDuration() {return duration;}
    public void setDuration(String duration) {this.duration = duration;}
}
