package com.flutterbackend.cart.domain;

import com.flutterbackend.eveythingPolicies.coverage_tiers.domain.CoverageTier;
import com.flutterbackend.eveythingPolicies.policies.domain.Policies;
import com.flutterbackend.user.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "cart_items",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_cart_user_policy",
        columnNames = {"user_id", "policy_id"}
    )
)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long cartItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policies policy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coverage_tier_id", nullable = false)
    private CoverageTier coverageTier;

    @Column(name = "added_at", updatable = false)
    private LocalDateTime addedAt;

    @PrePersist
    protected void onCreate() { this.addedAt = LocalDateTime.now(); }

    public CartItem() {}

    public Long getCartItemId()                   { return cartItemId; }
    public User getUser()                         { return user; }
    public void setUser(User user)                { this.user = user; }
    public Policies getPolicy()                   { return policy; }
    public void setPolicy(Policies policy)        { this.policy = policy; }
    public CoverageTier getCoverageTier()         { return coverageTier; }
    public void setCoverageTier(CoverageTier t)   { this.coverageTier = t; }
    public LocalDateTime getAddedAt()             { return addedAt; }
}
