package com.flutterbackend.payments.repository;

import com.flutterbackend.payments.domain.Payments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentsRepository extends JpaRepository<Payments, Long> {
    List<Payments> findByTransaction_TransactionId(Long transactionId);
}
