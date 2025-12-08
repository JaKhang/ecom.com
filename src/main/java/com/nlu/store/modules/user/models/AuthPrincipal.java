package com.nlu.store.modules.user.models;

import com.nlu.store.core.data.ULID;
import com.nlu.store.core.web.Authentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthPrincipal implements Authentication {

    private final User user;

    // Constructor bắt buộc để inject User vào
    public AuthPrincipal(User user) {
        this.user = user;
    }

    @Override
    public ULID id() {
        return user.getId();
    }

    @Override
    public String username() {
        // User entity không có field username, dùng email thay thế
        return user.getEmail();
    }

    @Override
    public boolean isVerified() {
        // Kiểm tra logic: đã có thời gian verify chưa
        return user.getVerifiedAt() != null;
    }

    @Override
    public boolean isActive() {
        return user.isActive();
    }

    @Override
    public List<String> authorities() {
        // Map từ List<Role> sang List<String> (dùng role code)
        if (user.getRoles() == null) return List.of();

        return user.getRoles().stream()
                .map(Role::getCode) // Giả sử Role có method getCode()
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> info() {
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("email", user.getEmail());
        userInfo.put("fullName", user.getFullName());
        userInfo.put("avatar", user.getAvatar());
        // userInfo.put("phone", user.getPhone()); // User chưa có field phone, tạm bỏ qua
        return userInfo;
    }
}
