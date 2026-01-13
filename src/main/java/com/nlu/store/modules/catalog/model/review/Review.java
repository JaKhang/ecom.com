package com.nlu.store.modules.catalog.model.review;


import com.nlu.store.core.data.ULID;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Getter
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

    @Builder
    public Review(boolean isVerified, ULID productId, ReviewStatus status, LocalDateTime updatedAt, LocalDateTime createdAt, ULID userId, String username, String comment, int rating, String avatar, ULID id) {
        this.isVerified = isVerified;
        this.productId = productId;
        this.status = status;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.userId = userId;
        this.username = username;
        this.comment = comment;
        this.rating = rating;
        this.avatar = avatar;
        this.id = id;
    }

    public Date asDate() {
        return java.util.Date.from(this.createdAt.atZone(ZoneId.systemDefault()).toInstant());
    }

}
