package com.nlu.store.modules.catalog.model.review;


import com.nlu.store.core.data.ULID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Review {
    private ULID id;
    private String avatar;
    private int rating;
    private String comment;
    private String username;
    private ULID userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ReviewStatus status;
    private ULID productId;
    private boolean isVerified;
    private String productName;


    @Builder
    public Review(ULID id, String avatar, int rating, String comment, String username, ULID userId, LocalDateTime createdAt, LocalDateTime updatedAt, ReviewStatus status, ULID productId, boolean isVerified, String productName) {
        this.id = id;
        this.avatar = avatar;
        this.rating = rating;
        this.comment = comment;
        this.username = username;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.productId = productId;
        this.isVerified = isVerified;
        this.productName = productName;
    }

    public Date asDate() {
        return java.util.Date.from(this.createdAt.atZone(ZoneId.systemDefault()).toInstant());
    }

}
