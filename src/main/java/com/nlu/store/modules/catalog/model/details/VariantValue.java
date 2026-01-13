package com.nlu.store.modules.catalog.model.details;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nlu.store.core.data.ULID;
import lombok.*;

import lombok.Data;

@Data
public class VariantValue {
    private ULID attributeId;     // ID của thuộc tính (VD: ID của Màu sắc)
    private ULID optionId;        // ID của tùy chọn (VD: ID của Đỏ)

    private String attributeLabel; // Tên hiển thị thuộc tính (VD: "Màu sắc")
    private String optionValue;    // Tên hiển thị tùy chọn (VD: "Đỏ")

    // Helper cho JSP: Chuyển ULID sang String
    @JsonIgnore
    public String getAttrIdString() { return attributeId != null ? attributeId.toString() : ""; }
    @JsonIgnore
    public String getOptIdString() { return optionId != null ? optionId.toString() : ""; }

    @Builder
    public VariantValue(ULID attributeId, ULID optionId, String attributeLabel, String optionValue) {
        this.attributeId = attributeId;
        this.optionId = optionId;
        this.attributeLabel = attributeLabel;
        this.optionValue = optionValue;
    }

    public VariantValue() {
    }
}
