package com.nlu.store.modules.catalog.dto;

import com.nlu.store.modules.catalog.model.AttributeType;

public record AttributeRequest(
        String name,
        String code,
        AttributeType type,
        String unit,
        boolean isVariantAxis,
        boolean isFilterable
) {

}