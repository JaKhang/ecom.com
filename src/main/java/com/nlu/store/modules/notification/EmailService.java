package com.nlu.store.modules.notification;

import java.time.Duration;

public interface EmailService {
    void sendVerifyToken(String email, String newToken, Duration tokenAge);

    void sendResetPassword(String email, String resetToken, Duration tokenAge);
}
