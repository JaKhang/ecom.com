package com.nlu.store.modules.payment.models;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Builder
@Getter
public class PaymentRequest {
    private String orderRefence;
    private BigDecimal amount;
    private String callbackUrl;
    private String description;
    private String ip;
}