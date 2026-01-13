package com.nlu.store.modules.payment.models;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentProvider {
    boolean supports(String provider);

    String name();

    String requestPayment(PaymentRequest request);

    PaymentResult handleCallback(HttpServletRequest request);
}
