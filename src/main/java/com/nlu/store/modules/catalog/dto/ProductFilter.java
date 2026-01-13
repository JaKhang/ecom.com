package com.nlu.store.modules.catalog.dto;

import com.nlu.store.core.data.ULID;

import java.util.List;

public record ProductFilter(
        List<ULID> brandId,
        Double lowPrice,
        Double highPrice,
        ULID categoryId
) {
}
