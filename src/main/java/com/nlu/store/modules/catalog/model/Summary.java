package com.nlu.store.modules.catalog.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Summary {
    private final int quantity;
    private final BigDecimal total;
    private final BigDecimal subtotal;
    private final BigDecimal discount;

    @Builder
    public Summary(int quantity, BigDecimal total, BigDecimal subtotal, BigDecimal discount) {
        this.quantity = quantity;
        this.total = total;
        this.subtotal = subtotal;
        this.discount = discount;
    }
}
