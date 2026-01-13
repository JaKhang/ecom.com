package com.nlu.store.core.email;

import java.io.File;
import java.util.List;

/**
 * Interface defining email sending operations.
 */
public interface EmailSender {

    /**
     * Sends a plain text email.
     *
     * @param to      Recipient's email address.
     * @param subject Email subject.
     * @param content Plain text content.
     * @return true if sent successfully, false otherwise.
     */
    boolean sendText(String to, String subject, String content);

    /**
     * Sends an HTML email.
     *
     * @param to          Recipient's email address.
     * @param subject     Email subject.
     * @param htmlContent HTML content (supports tags like <p>, <b>, <table>, etc.).
     * @return true if sent successfully, false otherwise.
     */
    boolean sendHtml(String to, String subject, String htmlContent);

    /**
     * Sends a comprehensive email with support for HTML and attachments.
     *
     * @param to          Recipient's email address.
     * @param subject     Email subject.
     * @param content     Email content (Text or HTML).
     * @param attachments List of files to attach (can be null).
     * @return true if sent successfully, false otherwise.
     */
    boolean send(String to, String subject, String content, List<File> attachments);
}
