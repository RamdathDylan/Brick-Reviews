package io.github.ramdathdylan.brickreviews.email;

public interface EmailService {

    void sendVerificationEmail(String to, String link);

    void sendAccountExistsEmail(String to);
}
