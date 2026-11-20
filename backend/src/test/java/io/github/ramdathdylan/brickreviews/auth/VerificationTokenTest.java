package io.github.ramdathdylan.brickreviews.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import io.github.ramdathdylan.brickreviews.user.User;

public class VerificationTokenTest {

    private static final Instant START = Instant.parse("2026-01-01T00:00:00Z");

    private static final Instant EXPIRES_AT = START.plus(Duration.ofHours(24));

    private final User testUser = new User("test@g.rit.edu", "hash");
 
    private VerificationToken makeToken() {
        return new VerificationToken(testUser, "token-hash", EXPIRES_AT);
    }

    @Test
    public void isFreshTokenUsable(){
        VerificationToken token = makeToken();

        assertThat(token.isUsable(START)).isTrue();
        assertThat(token.isExpired(START)).isFalse();
    }

    @Test 
    public void isUsedTokenUsable(){
        VerificationToken token = makeToken();
        token.markUsed();

        assertThat(token.isUsable(START)).isFalse();
        assertThat(token.isExpired(START)).isFalse();
    }

    @Test 
    public void isExpiredTokenUsable(){
        VerificationToken token = makeToken();
        Instant afterExpiry = EXPIRES_AT.plusSeconds(1);

        assertThat(token.isExpired(afterExpiry)).isTrue();
        assertThat(token.isUsable(afterExpiry)).isFalse();
    }

    @Test 
    public void isTokenUsableAtExpiry(){
       VerificationToken token = makeToken();

        assertThat(token.isExpired(EXPIRES_AT)).isTrue();
        assertThat(token.isUsable(EXPIRES_AT)).isFalse();
    }
}
