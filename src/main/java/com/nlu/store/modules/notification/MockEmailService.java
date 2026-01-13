package com.nlu.store.modules.notification;

import com.nlu.store.core.config.PropertySource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;

public class MockEmailService implements EmailService {

    private final String verifyUrl;

    @Inject
    public MockEmailService(PropertySource propertySource) {
        this.verifyUrl = propertySource.getProperty("notification.verify-url");
    }

    @Override
    public void sendVerifyToken(String email, String newToken, Duration tokenAge) {
        // Simulate sending a verification token email
        System.out.println("Sending verification email to: " + email);
        System.out.println("Verification token: " + newToken);
        System.out.println("Token age (in seconds): " + tokenAge.getSeconds());
        System.out.println("url: " + verifyUrl + "?email=" + email +"&&token=" + newToken);
        System.out.println("Email sent successfully (mock).");
    }

    @Override
    public void sendResetPassword(String email, String resetToken, Duration tokenAge) {
        // Simulate sending a reset password email
        System.out.println("Sending reset password email to: " + email);
        System.out.println("Reset token: " + resetToken);
        System.out.println("Token age (in seconds): " + tokenAge.getSeconds());
        System.out.println("Email sent successfully (mock).");
    }
}

