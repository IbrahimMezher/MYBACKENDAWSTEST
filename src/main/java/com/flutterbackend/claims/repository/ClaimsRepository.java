package com.flutterbackend.claims.repository;

import com.flutterbackend.claims.domain.Claims;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClaimsRepository extends JpaRepository<Claims, Long>{
    List<Claims> findByTransaction_User_UserId(Long userId);
    List<Claims> findByTransaction_TransactionId(Long transactionId);
    List<Claims> findByTransaction_Policy_Broker_User_UserId(Long userId);
}
