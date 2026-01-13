package com.nlu.store.modules.order.models;

public enum OrderStatus {
    PENDING,        // Order placed, waiting for action
    CONFIRMED,      // Confirmed by admin/system
    PROCESSING,     // Being packed
    SHIPPING,       // Handed over to carrier
    DELIVERED,      // Successfully delivered
    CANCELLED,      // Cancelled by user or admin
    RETURNED,       // Returned by customer
    REFUNDED        // Money sent back
}
