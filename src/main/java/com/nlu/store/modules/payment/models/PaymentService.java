package com.nlu.store.modules.payment.models;

import com.nlu.store.core.data.ULID;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentService {
    String requestPayment(PaymentMethod method, PaymentRequest request);
    PaymentResult verify(PaymentMethod method, Map<String, String> params);
}
