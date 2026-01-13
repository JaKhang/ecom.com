package com.nlu.store.modules.user.models;

import com.nlu.store.core.data.AbstractModel;
import com.nlu.store.core.data.ULID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class User extends AbstractModel implements Serializable {

    private String email;
    private String passwordHash;
    private String fullName;
    private boolean isActive;
    private LocalDateTime deletedAt;
    private LocalDateTime verifiedAt;
    private String verifyToken;
    private LocalDateTime verifyTokenExpiredAt;
    private String resetPasswordToken;
    private String avatar;
    private LocalDateTime resetPasswordTokenExpiredAt;


    private List<Role> roles = new ArrayList<>();

    @Builder

    public User(ULID id, LocalDateTime createdAt, LocalDateTime updatedAt, String email, String passwordHash, String fullName, boolean isActive, LocalDateTime deletedAt, LocalDateTime verifiedAt, String verifyToken, LocalDateTime verifyTokenExpiredAt, String resetPasswordToken, String avatar, LocalDateTime resetPasswordTokenExpiredAt, List<Role> roles) {
        super(id, createdAt, updatedAt);
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.isActive = isActive;
        this.deletedAt = deletedAt;
        this.verifiedAt = verifiedAt;
        this.verifyToken = verifyToken;
        this.verifyTokenExpiredAt = verifyTokenExpiredAt;
        this.resetPasswordToken = resetPasswordToken;
        this.avatar = avatar;
        this.resetPasswordTokenExpiredAt = resetPasswordTokenExpiredAt;
        this.roles = roles;
    }
}
