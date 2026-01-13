package com.nlu.store.modules.catalog.model;

import com.nlu.store.core.data.ULID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@Data
public class SimpleVariant {
    private ULID id;
    private String name;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private int stocks;
    private String sku;
    private String thumbnail;
    private ULID productId;
    private String slug;
    private double ratingAvg;
    private int reviewsCount;
}
