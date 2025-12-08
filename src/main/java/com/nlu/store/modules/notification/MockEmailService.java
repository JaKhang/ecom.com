package com.nlu.store.modules.notification;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MockEmailService implements EmailService {

    @Override
    public void sendVerifyToken(String email, String newToken, long tokenAge) {
        // Simulate sending a verification token email
        System.out.println("Sending verification email to: " + email);
        System.out.println("Verification token: " + newToken);
        System.out.println("Token age (in seconds): " + tokenAge);
        System.out.println("Email sent successfully (mock).");
    }

    @Override
    public void sendResetPassword(String email, String resetToken, long tokenAge) {
        // Simulate sending a reset password email
        System.out.println("Sending reset password email to: " + email);
        System.out.println("Reset token: " + resetToken);
        System.out.println("Token age (in seconds): " + tokenAge);
        System.out.println("Email sent successfully (mock).");
    }
}

