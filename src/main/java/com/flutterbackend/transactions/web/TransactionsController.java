package com.flutterbackend.transactions.web;

import com.flutterbackend.transactions.domain.Transactions;
import com.flutterbackend.transactions.dto.TransactionRequest;
import com.flutterbackend.transactions.dto.TransactionStatusRequest;
import com.flutterbackend.transactions.service.TransactionsService;
import com.flutterbackend.user.domain.User;
import com.flutterbackend.util.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = "*")
public class TransactionsController {

    private final TransactionsService transactionsService;
    private final CurrentUser currentUser;

    public TransactionsController(TransactionsService transactionsService, CurrentUser currentUser) {
        this.transactionsService = transactionsService;
        this.currentUser = currentUser;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('customer')")
    public Transactions create(@RequestBody TransactionRequest body, HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return transactionsService.create(body, user);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('customer', 'admin', 'superadmin')")
    public List<Transactions> getMyTransactions(HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return transactionsService.getByUser(user);
    }

    @GetMapping("/broker")
    @PreAuthorize("hasAnyRole('broker', 'admin', 'superadmin')")
    public List<Transactions> getBrokerTransactions(HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return transactionsService.getByBroker(user);
    }

    @PutMapping("/{id}/broker-approve")
    @PreAuthorize("hasAnyRole('broker', 'admin', 'superadmin')")
    public String brokerApprove(@PathVariable Long id, HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return transactionsService.brokerDecision(id, "ACCEPTED", user);
    }

    @PutMapping("/{id}/broker-reject")
    @PreAuthorize("hasAnyRole('broker', 'admin', 'superadmin')")
    public String brokerReject(@PathVariable Long id, HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return transactionsService.brokerDecision(id, "REJECTED", user);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public String updateStatus(@PathVariable Long id, @RequestBody TransactionStatusRequest body) {
        return transactionsService.updateStatus(id, body);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public List<Transactions> getAll() {
        return transactionsService.getAll();
    }
}
