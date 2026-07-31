package com.flutterbackend.checkout.service.impl;

import com.flutterbackend.address.domain.Address;
import com.flutterbackend.address.repository.AddressRepository;
import com.flutterbackend.cart.domain.CartItem;
import com.flutterbackend.cart.service.CartService;
import com.flutterbackend.checkout.dto.CheckoutRequest;
import com.flutterbackend.checkout.dto.CheckoutResponse;
import com.flutterbackend.checkout.service.CheckoutService;
import com.flutterbackend.notifications.service.NotificationService;
import com.flutterbackend.payments.domain.Payments;
import com.flutterbackend.payments.repository.PaymentsRepository;
import com.flutterbackend.transactions.domain.DeliveryStatus;
import com.flutterbackend.transactions.domain.Transactions;
import com.flutterbackend.transactions.repository.TransactionsRepository;
import com.flutterbackend.user.domain.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CheckoutServiceImpl implements CheckoutService {

    private final CartService cartService;
    private final TransactionsRepository transactionsRepository;
    private final PaymentsRepository paymentsRepository;
    private final AddressRepository addressRepository;
    private final NotificationService notificationService;

    public CheckoutServiceImpl(CartService cartService,
                               TransactionsRepository transactionsRepository,
                               PaymentsRepository paymentsRepository,
                               AddressRepository addressRepository,
                               NotificationService notificationService) {
        this.cartService = cartService;
        this.transactionsRepository = transactionsRepository;
        this.paymentsRepository = paymentsRepository;
        this.addressRepository = addressRepository;
        this.notificationService = notificationService;
    }

    @Override
    public CheckoutResponse checkout(CheckoutRequest request, User user) {
        List<CartItem> cartItems = cartService.getCartEntities(user);
        if (cartItems.isEmpty())
            throw new RuntimeException("Your cart is empty. Add policies before checking out.");

        String deliveryAddress = resolveAddress(request, user);
        List<Long> transactionIds = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        String paymentMethod = request.getPaymentMethod();
        boolean isCod = paymentMethod != null && (
                paymentMethod.equalsIgnoreCase("COD")
                || paymentMethod.equalsIgnoreCase("CASH")
                || paymentMethod.equalsIgnoreCase("CASH_ON_DELIVERY"));

        BigDecimal countryFee = BigDecimal.ZERO;
        if (user.getCountry() != null && user.getCountry().getDeliveryPrice() != null) {
            countryFee = BigDecimal.valueOf(user.getCountry().getDeliveryPrice());
        }

        for (CartItem item : cartItems) {
            BigDecimal price = item.getCoverageTier().getPremiumPrice();

            BigDecimal itemDelivery = BigDecimal.ZERO;
            if (isCod) {
                if (item.getPolicy().getDeliveryPrice() != null) {
                    itemDelivery = item.getPolicy().getDeliveryPrice();
                } else {
                    itemDelivery = countryFee;
                }
            }

            Transactions transaction = new Transactions();
            transaction.setUser(user);
            transaction.setPolicy(item.getPolicy());
            transaction.setCoverageTier(item.getCoverageTier());
            transaction.setAmountPaid(price);
            transaction.setPaymentStatus("PENDING");
            transaction.setPurchaseDate(LocalDateTime.now());
            transaction.setDeliveryAddress(deliveryAddress);
            transaction.setPaymentMethod(paymentMethod);
            transaction.setDeliveryPrice(itemDelivery);
            transaction.setDeliveryStatus(
                    isCod ? DeliveryStatus.AWAITING_BROKER : DeliveryStatus.PENDING);
            transaction.setBrokerStatus("PENDING");
            transaction.setFieldValues(request.getFieldValues());

            Transactions savedTransaction = transactionsRepository.save(transaction);
            transactionIds.add(savedTransaction.getTransactionId());
            notificationService.create(
                    user,
                    "Policy application submitted",
                    "Your application for \"" + item.getPolicy().getPolicyName() + "\" is pending broker review.",
                    "POLICY");

            Payments payment = new Payments();
            payment.setTransaction(savedTransaction);
            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setPaymentStatus("PENDING");
            payment.setPaymentDate(LocalDateTime.now());
            paymentsRepository.save(payment);

            totalAmount = totalAmount.add(price).add(itemDelivery);
        }

        cartService.clearCart(user);

        return CheckoutResponse.builder()
                .message("Checkout successful! Your application is pending broker review.")
                .itemsPurchased(cartItems.size())
                .totalAmountPaid(totalAmount)
                .transactionIds(transactionIds)
                .build();
    }

    private String resolveAddress(CheckoutRequest request, User user) {
        if (request.getAddressId() != null) {
            Address address = addressRepository
                    .findByAddressIdAndUser_UserId(request.getAddressId(), user.getUserId())
                    .orElseThrow(() -> new RuntimeException("Address not found"));
            return address.getStreet() + ", "
                    + address.getCity() + ", "
                    + (address.getState() != null ? address.getState() + ", " : "")
                    + address.getPostalCode() + ", "
                    + address.getCountry();
        }
        if (request.getStreet() == null || request.getCity() == null)
            throw new RuntimeException("Please provide a delivery address or select a saved one");
        return request.getStreet() + ", "
                + request.getCity() + ", "
                + (request.getState() != null ? request.getState() + ", " : "")
                + request.getPostalCode() + ", "
                + request.getCountry();
    }
}
