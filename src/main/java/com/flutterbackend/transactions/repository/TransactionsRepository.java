package com.flutterbackend.transactions.repository;

import com.flutterbackend.transactions.domain.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionsRepository extends JpaRepository<Transactions, Long> {
    List<Transactions> findByUser_UserId(Long userId);
    List<Transactions> findByPolicy_Broker_User_UserId(Long userId);

    boolean existsByUser_UserIdAndPolicy_PolicyIdAndBrokerStatus(
            Long userId, Long policyId, String brokerStatus);
}
