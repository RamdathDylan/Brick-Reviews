package io.github.ramdathdylan.brickreviews.email;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class SmtpEmailServiceTest {

    private static final String FROM = "no-reply@brickreviews.test";
    private static final String FRONTEND_URL = "http://localhost:4200";
    private static final String TO = "abc1234@g.rit.edu";

    private final JavaMailSender mailSender = mock(JavaMailSender.class);
    private final SmtpEmailService emailService = new SmtpEmailService(mailSender, FROM, FRONTEND_URL);

    @Test
    void verificationEmailContainsTheLinkAndExpiryNotice() {
    
        String link = FRONTEND_URL + "/verify?token=abc123";

        emailService.sendVerificationEmail(TO, link);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();

        assertThat(sent.getTo()).containsExactly(TO);
        assertThat(sent.getFrom()).isEqualTo(FROM);
        assertThat(sent.getText()).contains(link);
        assertThat(sent.getText()).contains("24 hours");
    }

    @Test 
    void accountExistsEmailContainsTheLoginLink() {
        String link = FRONTEND_URL + "/login";
        
        emailService.sendAccountExistsEmail(TO);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();

        assertThat(sent.getText()).contains(link);
        assertThat(sent.getTo()).containsExactly(TO);
        assertThat(sent.getFrom()).isEqualTo(FROM);

    }
}
