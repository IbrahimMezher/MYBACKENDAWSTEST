package com.flutterbackend.checkout.service;

import com.flutterbackend.checkout.dto.CheckoutRequest;
import com.flutterbackend.checkout.dto.CheckoutResponse;
import com.flutterbackend.user.domain.User;

public interface CheckoutService {
    CheckoutResponse checkout(CheckoutRequest request, User user);
}
