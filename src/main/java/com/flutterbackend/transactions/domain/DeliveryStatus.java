package com.flutterbackend.transactions.domain;

public enum DeliveryStatus {

    PENDING,
    COMPLETED,
    CANCELLED,

    AWAITING_BROKER,
    ACCEPTED_BY_BROKER,
    REJECTED_BY_BROKER,
    SHIPPED,
    DELIVERED,
    NOT_RECEIVED
}
