package com.nlu.store.modules.payment.models;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class DefaultPaymentService implements PaymentService{
    @Inject
    private  PaymentProvider paymentProviders;

    @Inject
    public DefaultPaymentService() {
    }


    @Override
    public String requestPayment(PaymentMethod method, PaymentRequest request) {
        return paymentProviders.requestPayment(request);
    }

    @Override
    public PaymentResult verify(PaymentMethod method, HttpServletRequest request) {
        return paymentProviders.handleCallback(request);
    }


}
