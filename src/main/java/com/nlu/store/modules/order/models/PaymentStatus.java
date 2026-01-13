package com.nlu.store.modules.order.models;

public enum PaymentStatus {
    UNPAID,         // Waiting for payment
    PAID,           // Payment received
    REFUNDED,       // Fully refunded
    PARTIAL_REFUND  // Partially refunded
}
