package io.github.ramdathdylan.brickreviews.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service 
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final String frontendURL;

    public SmtpEmailService(JavaMailSender mailSender, 
                    @Value("${app.mail.from}") String fromEmail,
                    @Value("${app.frontend-url}") String frontendURL){
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.frontendURL = frontendURL;
    }

    @Override
    public void sendVerificationEmail(String to, String link) {
        SimpleMailMessage verificationEmail = new SimpleMailMessage();
        String subject = "Brick Reviews | Verify Your Email";
        String body = """
        Hello, 

        Click here to verify your account: %s

        This link will expire after 24 hours.
        If you did not create a Brick Reviews account, you can ignore this email.

        Thank you,
        Brick Reviews
        """.formatted(link);
        
        verificationEmail.setFrom(fromEmail);
        verificationEmail.setTo(to);
        verificationEmail.setSubject(subject);
        verificationEmail.setText(body);

    
        mailSender.send(verificationEmail);
    }

    @Override
    public void sendAccountExistsEmail(String to) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
