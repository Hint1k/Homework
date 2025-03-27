package com.demo.finance.out.service.impl;

import com.demo.finance.out.service.EmailService;
import org.springframework.stereotype.Service;

/**
 * The EmailServiceImpl class simulates email notifications by printing details to the console.
 * It implements the EmailService interface for consistency with future real email services.
 */
@Service
public class EmailServiceImpl implements EmailService {

    /**
     * Simulates sending an email by printing details to the console.
     *
     * @param recipient the email address of the recipient
     * @param subject   the subject of the email
     * @param body      the body content of the email
     */
    @Override
    public void sendEmail(String recipient, String subject, String body) {
        System.out.println("=== Simulated Email Notification ===");
        System.out.println("To: " + recipient);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        System.out.println("===============================");

    }
}