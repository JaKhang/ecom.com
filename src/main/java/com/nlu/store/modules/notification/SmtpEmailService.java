package com.nlu.store.modules.notification;

import com.nlu.store.core.config.PropertySource;
import com.nlu.store.core.email.EmailSender;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;
@ApplicationScoped
public class SmtpEmailService implements EmailService {

    private final String verifyUrl;
    private final String resetUrl; // Added to handle reset password link
    private final EmailSender emailSender;

    @Inject
    public SmtpEmailService(PropertySource propertySource, EmailSender emailSender) {
        // Fetch URLs from configuration/properties
        this.verifyUrl = propertySource.getProperty("notification.verify-url");
        // Assuming you have a property for reset password, or derive it from verifyUrl
        this.resetUrl = propertySource.getProperty("notification.reset-password-url");
        this.emailSender = emailSender;
    }

    @Override
    public void sendVerifyToken(String email, String newToken, Duration tokenAge) {
        // 1. Construct the full URL
        String fullLink = verifyUrl + "?email=" + email +"&&token=" + newToken;
        long minutes = tokenAge.toMinutes();

        // 2. Generate HTML using Java Text Blocks
        String htmlContent = generateEmailBody(
                "Welcome to Ecom Store",
                "Please verify your email address to activate your account.",
                "Verify Account",
                fullLink,
                "This link will expire in " + minutes + " minutes."
        );

        // 3. Send email asynchronously (fire and forget)
        // Note: In a real app, use a ManagedExecutorService or @Asynchronous
        new Thread(() -> {
            emailSender.sendHtml(email, "Verify your account", htmlContent);
        }).start();
    }

    @Override
    public void sendResetPassword(String email, String resetToken, Duration tokenAge) {
        // 1. Construct the full URL
        // If resetUrl is null, fallback to a default path relative to verifyUrl or similar
        String targetUrl = (resetUrl != null ? resetUrl : "http://localhost:8080/reset") + "?token=" + resetToken;
        long minutes = tokenAge.toMinutes();

        // 2. Generate HTML
        String htmlContent = generateEmailBody(
                "Reset Your Password",
                "We received a request to reset your password. If you didn't make this request, please ignore this email.",
                "Reset Password",
                targetUrl,
                "This link is valid for " + minutes + " minutes."
        );

        // 3. Send email
        new Thread(() -> {
            emailSender.sendHtml(email, "Reset Password Request", htmlContent);
        }).start();
    }

    /**
     * Helper method to generate HTML string without a template engine.
     * Uses Java Text Blocks (""") for readability.
     *
     * @param title      The main heading of the email.
     * @param message    The body text.
     * @param buttonText The label on the action button.
     * @param link       The URL the button points to.
     * @param footerNote Small text at the bottom (e.g., expiration).
     * @return Formatted HTML String.
     */
    private String generateEmailBody(String title, String message, String buttonText, String link, String footerNote) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f6f6f6; margin: 0; padding: 20px; }
                    .container { background-color: #ffffff; max-width: 600px; margin: 0 auto; padding: 30px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
                    .header h2 { color: #333333; text-align: center; margin-bottom: 20px; }
                    .content { color: #555555; line-height: 1.6; font-size: 16px; }
                    .btn-wrap { text-align: center; margin: 30px 0; }
                    .btn { background-color: #007bff; color: #ffffff !important; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block; }
                    .footer { font-size: 12px; color: #999999; text-align: center; margin-top: 30px; border-top: 1px solid #eeeeee; padding-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>%s</h2>
                    </div>
                    <div class="content">
                        <p>%s</p>
                        <div class="btn-wrap">
                            <a href="%s" class="btn">%s</a>
                        </div>
                        <p style="font-size: 14px; color: #777;">%s</p>
                        <p style="font-size: 12px; margin-top: 20px;">
                            Or copy this link: <br/>
                            <a href="%3$s" style="color: #007bff;">%3$s</a>
                        </p>
                    </div>
                    <div class="footer">
                        &copy; 2026 NLU Store System. All rights reserved.
                    </div>
                </div>
            </body>
            </html>
            """.formatted(title, message, link, buttonText, footerNote);
    }
}