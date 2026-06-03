package com.nlu.store.modules.payment.models;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class PaymentResult {
    private boolean paid;
    private String transactionId;
    private String note;
    private String orderRef;
    private Map<String, String> params;
}
