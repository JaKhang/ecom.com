package com.nlu.store.modules.catalog.dto;

import com.nlu.store.core.data.ULID;
import lombok.Data;

@Data
public class SearchRequest {
    private String keyword;
    private ULID categoryId;
}
