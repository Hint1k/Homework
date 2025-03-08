package com.demo.finance.domain.utils;

public class MockEmailUtils {
    public void sendEmail(String recipient, String subject, String body) {
        System.out.println("=== Simulated Email Notification ===");
        System.out.println("To: " + recipient);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        System.out.println("===============================");
    }
}