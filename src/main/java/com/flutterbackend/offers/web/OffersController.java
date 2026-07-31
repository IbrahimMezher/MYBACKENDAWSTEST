package com.flutterbackend.offers.web;

import com.flutterbackend.offers.domain.Offers;
import com.flutterbackend.offers.dto.OfferRequest;
import com.flutterbackend.offers.service.OffersService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/offers")
@CrossOrigin(origins = "*")
public class OffersController {

    private final OffersService offersService;

    public OffersController(OffersService offersService) {
        this.offersService = offersService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('broker', 'admin', 'superadmin')")
    public String create(@RequestBody OfferRequest body) {
        return offersService.create(body);
    }

    @GetMapping
    public List<Offers> getAll() {
        return offersService.getAll();
    }

    @GetMapping("/broker/{brokerId}")
    public List<Offers> getByBroker(@PathVariable Long brokerId) {
        return offersService.getByBroker(brokerId);
    }

    @GetMapping("/policy/{policyId}")
    public List<Offers> getByPolicy(@PathVariable Long policyId) {
        return offersService.getByPolicy(policyId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('broker', 'admin', 'superadmin')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return offersService.delete(id);
    }
}
