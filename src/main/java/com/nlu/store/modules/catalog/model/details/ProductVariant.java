package com.nlu.store.modules.catalog.model.details;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nlu.store.core.data.AbstractModel;
import com.nlu.store.core.data.ULID;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Getter
@Setter
public class ProductVariant extends AbstractModel {
    private String sku;
    private String name; // Thường là: "Áo Thun - Đỏ - XL"
    private BigDecimal price;
    private BigDecimal originalPrice;
    private int stocks;
    private int weight; // Đơn vị gram
    private boolean isDefault; // True: Chọn sẵn khi load trang

    private List<VariantValue> values = new ArrayList<>();

    @Builder

    public ProductVariant(ULID id, LocalDateTime createdAt, LocalDateTime updatedAt, String sku, String name, BigDecimal price, BigDecimal originalPrice, int stocks, int weight, boolean isDefault, List<VariantValue> values) {
        super(id, createdAt, updatedAt);
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.originalPrice = originalPrice;
        this.stocks = stocks;
        this.weight = weight;
        this.isDefault = isDefault;
        this.values = values;
    }

    public ProductVariant() {
    }

    @JsonIgnore
    public String getCombinationKey() {
        if (values == null || values.isEmpty()) return "";
        return values.stream()
                .map((i) -> i.getOptionId().toString()) // Lấy ID của option (VD: opt_red)
                .sorted() // Bắt buộc sort để đảm bảo thứ tự luôn giống nhau (A-B giống B-A)
                .collect(Collectors.joining("-"));
    }

    public static void main(String[] args) {
        StringJoiner stringJoiner = new StringJoiner(",");
        for (int i = 0; i < 200; i++) {
            stringJoiner.add("\"" + ULID.fast() + "\"");
        }

        System.out.println("[" + stringJoiner + "]");
    }
}
