package com.nlu.store.modules.order.services;

import lombok.Builder;
import lombok.Getter;

import java.rmi.server.UID;

@Builder
@Getter
public class CheckoutResult {
    private String orderCode;
    private String paymentGetWay;
}
