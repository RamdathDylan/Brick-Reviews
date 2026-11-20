package io.github.ramdathdylan.brickreviews.auth;

import java.time.Instant;

import io.github.ramdathdylan.brickreviews.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table(name="verification_tokens")
public class VerificationToken {

    @Id 
    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name="token_hash", nullable=false, unique=true, length=64)
    private String tokenHash;

    @Column(name="expires_at", nullable=false)
    private Instant expiresAt;

    @Column(name="used", nullable=false)
    private boolean used = false;

    @Column(name="created_at", nullable=false, updatable=false)
    private Instant createdAt = Instant.now();

    protected VerificationToken(){
        //for jpa
    }

    public VerificationToken(User user, String tokenHash, Instant expiresAt){
        this.user = user;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired(Instant now){
        return !now.isBefore(expiresAt);
    }

    public boolean isUsable(Instant now){
        return now.compareTo(expiresAt) < 0 && used == false;
    }

    public void markUsed(){
        used = true;
    }

    public Long getId(){
        return id;
    }

    public User getUser(){
        return user;
    }

    public String getTokenHash(){
        return tokenHash;
    }

    public Instant getExpiresAt(){
        return expiresAt;
    }

    public boolean isUsed(){
        return used;
    }

    public Instant getCreatedAt(){
        return createdAt;
    }
}
