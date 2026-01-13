package com.nlu.store.modules.payment.models;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.Map;
@ApplicationScoped
public class MockPaymentProvider implements PaymentProvider {


    @Override
    public boolean supports(String provider) {
        return false;
    }

    @Override
    public String name() {
        return "";
    }

    @Override
    public String requestPayment(PaymentRequest request) {
        return "";
    }

    @Override
    public PaymentResult handleCallback(HttpServletRequest request) {
        return null;
    }
}
