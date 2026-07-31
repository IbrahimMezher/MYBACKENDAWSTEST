package com.flutterbackend.broker.repository;

import com.flutterbackend.broker.domain.Broker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrokerRepository extends JpaRepository<Broker, Long> {
    Optional<Broker> findByUser_UserId(Long userId);

}
