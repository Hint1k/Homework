package com.demo.finance.out.service;

public interface EmailService {

    void sendEmail(String recipient, String subject, String body);
}