package com.nlu.store.modules.order.models;

import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.payment.models.PaymentMethod;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter

public class Order implements Serializable {
    private ULID id;

    // Human-readable order code (e.g., #ORD-2024-001)
    private String code;

    private ULID userId;

    // --- Status & Lifecycle ---
    private OrderStatus status;

    // Reason if the order is cancelled (e.g., "Out of stock", "Customer changed mind")
    private String cancelReason;

    // --- Payment & Reconciliation ---
    private PaymentMethod paymentMethod; // e.g., "COD", "VNPAY", "BANKING"
    private PaymentStatus paymentStatus;

    // Transaction ID from payment gateway (e.g., VNPAY_12345678) for reconciliation
    private String transactionRef;

    // Exact time when payment was received
    private LocalDateTime paidAt;

    // --- Financials ---
    private String currency; // Default: "VND"
    private BigDecimal subTotal;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;

    // The coupon code applied (for marketing tracking)
    private String couponCode;

    // Final amount to be paid (subTotal + shipping - discount)
    private BigDecimal grandTotal;

    // --- Audit & Meta ---
    private String note; // Customer's note
    private String ipAddress; // To detect fraud/spam
    private String userAgent; // Device information

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- Relationships ---
    @Setter
    private List<OrderItem> items;
    private ShippingDetails shippingDetails;

    // Getters & Setters...
}
