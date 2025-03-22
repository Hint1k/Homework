package com.demo.finance.out.service;

/**
 * The EmailService interface defines the methods for sending emails.
 * It provides a contract for implementing real or mock email services.
 */
public interface EmailService {

    /**
     * Sends an email to the specified recipient.
     *
     * @param recipient the email address of the recipient
     * @param subject   the subject of the email
     * @param body      the body content of the email
     */
    void sendEmail(String recipient, String subject, String body);
}