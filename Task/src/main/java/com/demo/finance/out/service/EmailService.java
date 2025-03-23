package com.demo.finance.out.service;

/**
 * The {@code EmailService} interface defines the contract for operations related to sending emails.
 * It provides a method to send an email with a specified recipient, subject, and body.
 */
public interface EmailService {

    /**
     * Sends an email to the specified recipient with the given subject and body.
     *
     * @param recipient the email address of the recipient
     * @param subject   the subject of the email
     * @param body      the content or body of the email
     */
    void sendEmail(String recipient, String subject, String body);
}