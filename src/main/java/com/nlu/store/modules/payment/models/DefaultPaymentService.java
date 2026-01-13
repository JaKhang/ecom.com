package com.nlu.store.modules.payment.models;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class DefaultPaymentService implements PaymentService{
    private  List<PaymentProvider> paymentProviders;

    @Inject
    public DefaultPaymentService() {
    }


    @Override
    public String requestPayment(PaymentMethod method, PaymentRequest request) {
        return "";
    }

    @Override
    public PaymentResult verify(PaymentMethod method, Map<String, String> params) {
        return null;
    }

    private PaymentProvider get(PaymentMethod method){
        return paymentProviders.stream().filter(paymentProvider -> paymentProvider.supports(method.toString())).findFirst().orElseThrow();
    }
}
