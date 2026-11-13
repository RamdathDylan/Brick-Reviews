package io.github.ramdathdylan.brickreviews.config;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderTest {
    
    SecurityConfig config = new SecurityConfig();
    
    private static final String PASSWORD = "IlovePasswords123/";

    @Test 
    void encodedPasswordIsNotPlainText() {
        PasswordEncoder encoder = config.passwordEncoder();

        String passwordHash = encoder.encode(PASSWORD);

        assertThat(passwordHash).isNotEqualTo(PASSWORD);
        
    }

    @Test 
    void encodedPasswordUsesArgon2id() {
        PasswordEncoder encoder = config.passwordEncoder();

        String passwordHash = encoder.encode(PASSWORD);

        assertThat(passwordHash).startsWith("$argon2id$");
        
    }

    @Test 
    void samePasswordEncodesDifferentlyEachTime() {
        PasswordEncoder encoder = config.passwordEncoder();

        String firstHash = encoder.encode(PASSWORD);
        String secondHash = encoder.encode(PASSWORD);

        assertThat(firstHash).isNotEqualTo(secondHash);
    }

    @Test 
    void matchesAcceptsCorrectPasswordAndRejectsWrongOne() {
        PasswordEncoder encoder = config.passwordEncoder();
        String passwordHash = encoder.encode(PASSWORD);

        assertThat(encoder.matches(PASSWORD, passwordHash)).isTrue();
        assertThat(encoder.matches("Idontlikepasswords1!32", passwordHash)).isFalse();

    }
}
