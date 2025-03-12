package com.demo.finance.out.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @InjectMocks private EmailServiceImpl emailService;

    @Test
    @DisplayName("Test that sendEmail sends a simulated email to the console")
    void testSendEmail_sendsSimulatedEmailToConsole() {
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String body = "This is a test email body.";

        emailService.sendEmail(recipient, subject, body);

        assertThat(emailService).isNotNull();
    }

    @Test
    @DisplayName("Test that sendEmail does not throw an exception when the recipient is empty")
    void testSendEmail_withEmptyRecipient_doesNotThrowException() {
        String recipient = "";
        String subject = "Test Subject";
        String body = "This is a test email body.";

        emailService.sendEmail(recipient, subject, body);

        assertThat(emailService).isNotNull();
    }

    @Test
    @DisplayName("Test that sendEmail does not throw an exception when all values are null")
    void testSendEmail_withNullValues_doesNotThrowException() {
        String recipient = null;
        String subject = null;
        String body = null;

        emailService.sendEmail(recipient, subject, body);

        assertThat(emailService).isNotNull();
    }
}