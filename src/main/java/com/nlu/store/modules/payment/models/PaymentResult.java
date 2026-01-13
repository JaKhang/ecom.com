package com.nlu.store.modules.payment.models;

import java.util.Map;

public class PaymentResult {
    private boolean paid;
    private String transactionId;
    private String note;
    private Map<String, String> params;
}
