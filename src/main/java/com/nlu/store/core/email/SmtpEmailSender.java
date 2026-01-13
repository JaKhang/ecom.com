package com.nlu.store.core.email;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Implementation of EmailSender using Jakarta Mail (SMTP).
 * Compatible with Tomcat 10+.
 */
public class SmtpEmailSender implements EmailSender {

    private final String host;
    private final String port;
    private final String username; // Sender's email
    private final String password; // App Password (not the login password)
    private final boolean auth;
    private final boolean starttls;

    /**
     * Constructor to initialize SMTP server details.
     *
     * @param host     SMTP Host (e.g., smtp.gmail.com)
     * @param port     SMTP Port (e.g., 587 for TLS)
     * @param username Sender email address
     * @param password Sender App Password
     */
    public SmtpEmailSender(String host, String port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.auth = true;      // Enable authentication by default
        this.starttls = true;  // Enable STARTTLS by default
    }

    public SmtpEmailSender(String host, String port, String username, String password, boolean auth, boolean starttls) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.auth = auth;
        this.starttls = starttls;
    }

    @Override
    public boolean sendText(String to, String subject, String content) {
        return sendEmailInternal(to, subject, content, false, null);
    }

    @Override
    public boolean sendHtml(String to, String subject, String htmlContent) {
        return sendEmailInternal(to, subject, htmlContent, true, null);
    }

    @Override
    public boolean send(String to, String subject, String content, List<File> attachments) {
        return sendEmailInternal(to, subject, content, true, attachments);
    }

    /**
     * Internal helper method to handle the low-level JavaMail logic.
     *
     * @param to          Recipient email
     * @param subject     Email subject
     * @param content     Email body
     * @param isHtml      Flag to indicate if content is HTML
     * @param attachments List of files to attach
     * @return true if success
     */
    private boolean sendEmailInternal(String to, String subject, String content, boolean isHtml, List<File> attachments) {
        try {
            // 1. Setup SMTP Properties
            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", String.valueOf(auth));
            props.put("mail.smtp.starttls.enable", String.valueOf(starttls));

            // Ensure TLS v1.2 is used (crucial for modern Gmail security)
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.ssl.trust", host);

            // 2. Create Session with Authenticator
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            // 3. Create the Message
            Message message = new MimeMessage(session);
            // Set "From" address with a Display Name to reduce spam score
            message.setFrom(new InternetAddress(username, "NLU Store System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setSentDate(new Date());

            // 4. Handle Content (Multipart)
            Multipart multipart = getMultipart(content, isHtml, attachments);

            // Set the multipart content to the message
            message.setContent(multipart);

            // 5. Send the email
            Transport.send(message);
            System.out.println("[Email Service] Email sent successfully to: " + to);
            return true;

        } catch (MessagingException | IOException e) {
            System.err.println("[Email Service] Error sending email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static @NonNull Multipart getMultipart(String content, boolean isHtml, List<File> attachments) throws MessagingException, IOException {
        Multipart multipart = new MimeMultipart();

        // 4.1 Create Body Part (Text or HTML)
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        if (isHtml) {
            messageBodyPart.setContent(content, "text/html; charset=UTF-8");
        } else {
            messageBodyPart.setText(content, "UTF-8");
        }
        multipart.addBodyPart(messageBodyPart);

        // 4.2 Handle Attachments (if any)
        if (attachments != null && !attachments.isEmpty()) {
            for (File file : attachments) {
                if (file.exists()) {
                    MimeBodyPart attachPart = new MimeBodyPart();
                    attachPart.attachFile(file);
                    multipart.addBodyPart(attachPart);
                }
            }
        }
        return multipart;
    }
}
