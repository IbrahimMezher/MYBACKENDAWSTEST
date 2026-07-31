package com.flutterbackend.offers.repository;

import com.flutterbackend.offers.domain.Offers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OffersRepository extends JpaRepository<Offers, Long> {
    List<Offers> findByBroker_BrokerId(Long brokerId);
    List<Offers> findByPolicy_PolicyId(Long policyId);
}
