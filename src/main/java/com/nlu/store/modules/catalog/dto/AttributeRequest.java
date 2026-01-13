package com.nlu.store.modules.catalog.dto;


public record AttributeRequest(
        String name,
        String code,
        String unit,
        boolean isVariantAxis,
        boolean isFilterable
) {

}