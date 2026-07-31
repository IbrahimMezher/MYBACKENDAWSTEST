package com.flutterbackend.wishlist.domain;

import com.flutterbackend.eveythingPolicies.policies.domain.Policies;
import com.flutterbackend.user.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "wishlist_items",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_wishlist_user_policy",
        columnNames = {"user_id", "policy_id"}
    )
)
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_item_id")
    private Long wishlistItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policies policy;

    @Column(name = "added_at", updatable = false)
    private LocalDateTime addedAt;

    @PrePersist
    protected void onCreate() { this.addedAt = LocalDateTime.now(); }

    public WishlistItem() {}

    public Long getWishlistItemId() { return wishlistItemId; }
    public void setWishlistItemId(Long wishlistItemId) { this.wishlistItemId = wishlistItemId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Policies getPolicy() { return policy; }
    public void setPolicy(Policies policy) { this.policy = policy; }
    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}
