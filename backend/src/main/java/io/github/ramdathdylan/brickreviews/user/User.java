package io.github.ramdathdylan.brickreviews.user;
import java.time.Instant;
import java.util.Locale;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name="users")
public class User {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="email", nullable=false, unique=true)
    private String email;

    @Column(name="password_hash", nullable=false)
    private String passwordHash;

    @Column(name="role", nullable=false, length=20)
    @Enumerated(EnumType.STRING)
    private Role role = Role.STUDENT;

    @Column(name="verified", nullable=false)
    private boolean verified = false;

    @Column(name="status", nullable=false, length=20)
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name="created_at", nullable=false, updatable=false)
    private Instant createdAt = Instant.now();

    protected User(){
        //needed for jpa
    }

    public User(String email, String passwordHash){

        this.email = email.trim().toLowerCase(Locale.ROOT);
        this.passwordHash = passwordHash;
    }

    public boolean isAdmin(){
        return role == Role.ADMIN;
    }

    public boolean canContribute(){
        return status == UserStatus.ACTIVE && verified;
    }

    public void ban(){
        status = UserStatus.BANNED;
    }

    public void markVerified(){
        verified = true;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public boolean isVerified() {
        return verified;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt(){
        return createdAt;
    }
}
