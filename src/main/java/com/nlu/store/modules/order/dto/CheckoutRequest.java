package com.nlu.store.modules.order.dto;

import com.nlu.store.modules.payment.models.PaymentMethod;
import lombok.Data;

@Data
public class CheckoutRequest {
    private String fullName;
    private String phoneNumber;
    private String email;
    private String province;
    private String district;
    private String ward;
    private String addressDetail;
    private String note;
    private PaymentMethod paymentMethod;
}