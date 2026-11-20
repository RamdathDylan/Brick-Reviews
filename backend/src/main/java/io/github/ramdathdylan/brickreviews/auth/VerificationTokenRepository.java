package io.github.ramdathdylan.brickreviews.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.ramdathdylan.brickreviews.user.User;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long>{
    Optional<VerificationToken> findByTokenHash(String tokenHash);

    List<VerificationToken> findAllByUserAndUsedFalse(User user);
}
