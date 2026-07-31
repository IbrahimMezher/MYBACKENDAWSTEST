package com.flutterbackend.payments.web;

import com.flutterbackend.payments.domain.Payments;
import com.flutterbackend.payments.dto.PaymentRequest;
import com.flutterbackend.payments.dto.PaymentStatusRequest;
import com.flutterbackend.payments.service.PaymentsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "*")
public class PaymentsController {

    private final PaymentsService paymentsService;

    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public String create(@RequestBody PaymentRequest body) {
        return paymentsService.create(body);
    }

    @GetMapping("/transaction/{transactionId}")
    public List<Payments> getByTransaction(@PathVariable Long transactionId) {
        return paymentsService.getByTransaction(transactionId);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public String updateStatus(@PathVariable Long id,
                               @RequestBody PaymentStatusRequest body) {
        return paymentsService.updateStatus(id, body);
    }
}
