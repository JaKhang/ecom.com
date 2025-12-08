package com.nlu.store.modules.notification;

public interface EmailService {
    void sendVerifyToken(String email, String newToken, long tokenAge);

    void sendResetPassword(String email, String resetToken, long tokenAge);
}
