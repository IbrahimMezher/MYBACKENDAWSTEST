package com.flutterbackend.claims.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flutterbackend.transactions.domain.Transactions;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
public class Claims {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long claimId;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    @JsonIgnoreProperties({"claims"})
    private Transactions transaction;

    private LocalDateTime claimDate;

    @Column(nullable = false)
    private String claimStatus;

    private BigDecimal claimAmount;
    private String remarks;

    public Claims() {}

    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }
    public Transactions getTransaction() { return transaction; }
    public void setTransaction(Transactions transaction) { this.transaction = transaction; }
    public LocalDateTime getClaimDate() { return claimDate; }
    public void setClaimDate(LocalDateTime claimDate) { this.claimDate = claimDate; }
    public String getClaimStatus() { return claimStatus; }
    public void setClaimStatus(String claimStatus) { this.claimStatus = claimStatus; }
    public BigDecimal getClaimAmount() { return claimAmount; }
    public void setClaimAmount(BigDecimal claimAmount) { this.claimAmount = claimAmount; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
