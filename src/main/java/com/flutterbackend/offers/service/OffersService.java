package com.flutterbackend.offers.service;

import com.flutterbackend.broker.domain.Broker;
import com.flutterbackend.broker.repository.BrokerRepository;
import com.flutterbackend.eveythingPolicies.policies.domain.Policies;
import com.flutterbackend.eveythingPolicies.policies.repository.PoliciesRepository;
import com.flutterbackend.offers.domain.Offers;
import com.flutterbackend.offers.dto.OfferRequest;
import com.flutterbackend.offers.repository.OffersRepository;
import org.springframework.http.ResponseEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class OffersService {

    private final OffersRepository offersRepository;
    private final BrokerRepository brokerRepository;
    private final PoliciesRepository policiesRepository;

    public OffersService(OffersRepository offersRepository,
                         BrokerRepository brokerRepository,
                         PoliciesRepository policiesRepository) {
        this.offersRepository = offersRepository;
        this.brokerRepository = brokerRepository;
        this.policiesRepository = policiesRepository;
    }

    public String create(OfferRequest body) {
        Broker broker = brokerRepository.findById(body.getBrokerId())
                .orElseThrow(() -> new RuntimeException("Broker not found"));

        Policies policy = policiesRepository.findById(body.getPolicyId())
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        Offers offer = new Offers();
        offer.setBroker(broker);
        offer.setPolicy(policy);
        offer.setDiscountPercentage(body.getDiscountPercentage());
        offer.setValidFrom(body.getValidFrom());
        offer.setValidUntil(body.getValidUntil());
        offer.setStatus(body.getStatus());

        offersRepository.save(offer);
        return "Offer created successfully.";
    }

    public List<Offers> getAll() { return offersRepository.findAll(); }

    public List<Offers> getByBroker(Long brokerId) {
        return offersRepository.findByBroker_BrokerId(brokerId);
    }

    public List<Offers> getByPolicy(Long policyId) {
        return offersRepository.findByPolicy_PolicyId(policyId);
    }

    public ResponseEntity<Void> delete(Long id) {
        if (!offersRepository.existsById(id)) return ResponseEntity.notFound().build();
        offersRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
