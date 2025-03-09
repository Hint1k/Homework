package com.demo.finance.domain.utils;

/**
 * The {@code MockEmailUtils} class simulates the process of sending email notifications.
 * This class is used for testing or simulating email communication without actually sending real emails.
 */
public class MockEmailUtils {

    /**
     * Simulates sending an email by printing the email details to the console.
     *
     * @param recipient The recipient's email address.
     * @param subject The subject of the email.
     * @param body The body content of the email.
     */
    public void sendEmail(String recipient, String subject, String body) {
        System.out.println("=== Simulated Email Notification ===");
        System.out.println("To: " + recipient);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        System.out.println("===============================");
    }
}