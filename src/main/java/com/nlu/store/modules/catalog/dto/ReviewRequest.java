package com.nlu.store.modules.catalog.dto;

import com.nlu.store.core.data.ULID;
import lombok.Data;

@Data
public class ReviewRequest {
    private String comment;
    private int rating;
    private ULID productId;
}
