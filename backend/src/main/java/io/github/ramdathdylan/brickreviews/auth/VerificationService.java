package io.github.ramdathdylan.brickreviews.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.ramdathdylan.brickreviews.email.EmailService;
import io.github.ramdathdylan.brickreviews.user.User;

@Service
public class VerificationService {

    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final Clock clock;
    private final String frontendURL;

    private final SecureRandom secureRandom = new SecureRandom();

    private static final Duration TOKEN_LIFETIME = Duration.ofHours(24);

    public VerificationService(VerificationTokenRepository tokenRepository,
                             EmailService emailService,
                             Clock clock, 
                             @Value("${app.frontend-url}") String frontendURL){
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.clock = clock;
        this.frontendURL = frontendURL;
    }

    @Transactional 
    public void issueToken(User user){
        List<VerificationToken> tokens = tokenRepository.findAllByUserAndUsedFalse(user);
        
        for (VerificationToken verificationToken : tokens) {
            verificationToken.markUsed();
        }

        String rawTokenString = generateRawToken();

        Instant expiryTime = clock.instant().plus(TOKEN_LIFETIME);
        
        VerificationToken token = new VerificationToken(user, hash(rawTokenString), expiryTime);
        tokenRepository.saveAndFlush(token);

        String link = frontendURL + "/verify?token=" + rawTokenString;
        emailService.sendVerificationEmail(user.getEmail(), link);
    }

    @Transactional 
    public User verify(String rawToken){
        if(rawToken == null || rawToken.isBlank()){
            throw new InvalidTokenException("Invalid or expired verification link");
        }

        VerificationToken token = tokenRepository.findByTokenHash(hash(rawToken))
        .orElseThrow(() -> new InvalidTokenException("Invalid or expired verification link"));

        if(!token.isUsable(clock.instant())){
            throw new InvalidTokenException("Invalid or expired verification link");
        }

        token.markUsed();

        token.getUser().markVerified();

        return token.getUser();
    }

    private String generateRawToken(){
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String rawToken){
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return  HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e){
            throw new IllegalStateException("SHA-256 not available", e); 
        }
    }

}
