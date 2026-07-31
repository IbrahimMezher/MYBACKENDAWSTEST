package com.flutterbackend.payments.service;

import com.flutterbackend.payments.domain.Payments;
import com.flutterbackend.payments.dto.PaymentRequest;
import com.flutterbackend.payments.dto.PaymentStatusRequest;
import com.flutterbackend.payments.repository.PaymentsRepository;
import com.flutterbackend.transactions.domain.Transactions;
import com.flutterbackend.transactions.repository.TransactionsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Service
public class PaymentsService {

    private final PaymentsRepository paymentsRepository;
    private final TransactionsRepository transactionsRepository;

    public PaymentsService(PaymentsRepository paymentsRepository,
                           TransactionsRepository transactionsRepository) {
        this.paymentsRepository = paymentsRepository;
        this.transactionsRepository = transactionsRepository;
    }

    public String create(PaymentRequest body) {
        Transactions transaction = transactionsRepository.findById(body.getTransactionId())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        Payments payment = new Payments();
        payment.setTransaction(transaction);
        payment.setPaymentMethod(body.getPaymentMethod());
        payment.setPaymentStatus("Pending");
        payment.setPaymentDate(LocalDateTime.now());

        paymentsRepository.save(payment);
        return "Payment created successfully.";
    }

    public List<Payments> getByTransaction(Long transactionId) {
        return paymentsRepository.findByTransaction_TransactionId(transactionId);
    }

    public String updateStatus(Long id, PaymentStatusRequest body) {
        Payments payment = paymentsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setPaymentStatus(body.getPaymentStatus());
        paymentsRepository.save(payment);
        return "Payment status updated.";
    }
}
