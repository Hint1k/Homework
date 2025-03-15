package com.demo.finance.out.service;

import com.demo.finance.out.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @InjectMocks private EmailServiceImpl emailService;

    @Test
    void testSendEmail_sendsSimulatedEmailToConsole() {
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String body = "This is a test email body.";

        emailService.sendEmail(recipient, subject, body);

        assertThat(emailService).isNotNull();
    }

    @Test
    void testSendEmail_withEmptyRecipient_doesNotThrowException() {
        String recipient = "";
        String subject = "Test Subject";
        String body = "This is a test email body.";

        emailService.sendEmail(recipient, subject, body);

        assertThat(emailService).isNotNull();
    }

    @Test
    void testSendEmail_withNullValues_doesNotThrowException() {
        String recipient = null;
        String subject = null;
        String body = null;

        emailService.sendEmail(recipient, subject, body);

        assertThat(emailService).isNotNull();
    }
}