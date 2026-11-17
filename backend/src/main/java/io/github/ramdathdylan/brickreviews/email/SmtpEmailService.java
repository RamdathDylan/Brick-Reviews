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
        SimpleMailMessage accountExistsEmail = new SimpleMailMessage();
        String subject = "Brick Reviews | Sign-up attempt for your account";
        String body = """
        Hello, 

        Someone tried to create a Brick Reviews account with this email address.

        If it was you, you already have an account and can log in here:
        %s
    

        If it wasn't you, you can ignore this email; no changes were made to your account.

        Thank you,
        Brick Reviews
        """.formatted(frontendURL+"/login");
        
        accountExistsEmail.setFrom(fromEmail);
        accountExistsEmail.setTo(to);
        accountExistsEmail.setSubject(subject);
        accountExistsEmail.setText(body);

        mailSender.send(accountExistsEmail);
    }
    
}
