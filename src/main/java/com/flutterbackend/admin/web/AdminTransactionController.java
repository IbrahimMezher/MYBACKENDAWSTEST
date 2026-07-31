package com.flutterbackend.admin.web;

import com.flutterbackend.transactions.domain.Transactions;
import com.flutterbackend.transactions.dto.TransactionStatusRequest;
import com.flutterbackend.transactions.service.TransactionsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/transactions")
@CrossOrigin(origins = "*")
public class AdminTransactionController {

    private final TransactionsService transactionsService;

    public AdminTransactionController(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public List<Transactions> getAllTransactions() {
        return transactionsService.getAll();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('customer','admin', 'superadmin')")
    public List<Transactions> getByUser(@PathVariable Long userId) {
        return transactionsService.getByUserId(userId);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public String updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return transactionsService.updatePaymentStatus(id, body.get("paymentStatus"));
    }
}
