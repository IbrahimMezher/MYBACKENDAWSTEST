package com.flutterbackend.eveythingPolicies.policy_exclusions.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "exclusion_types")
public class ExclusionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exclusionTypeId;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    public ExclusionType() {}

    public Long getExclusionTypeId() { return exclusionTypeId; }
    public void setExclusionTypeId(Long exclusionTypeId) { this.exclusionTypeId = exclusionTypeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
