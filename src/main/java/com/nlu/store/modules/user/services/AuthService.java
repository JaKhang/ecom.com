package com.nlu.store.modules.user.services;

import com.nlu.store.core.web.Authentication;
import com.nlu.store.modules.user.dto.LoginRequest;
import com.nlu.store.modules.user.dto.RegisterRequest;

public interface AuthService {
    Authentication login(LoginRequest request);

    void register(RegisterRequest request);

    void verify(String email, String token);

    long requestVerify(String email);

    void resetPassword(String token, String newPassword);

    void requestResetPassword(String email);

    long getRequestVerifyCountDown(String username);
}
