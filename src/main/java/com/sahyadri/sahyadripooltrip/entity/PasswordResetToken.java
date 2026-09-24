package com.sahyadri.sahyadripooltrip.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "password_reset_tokens", indexes = { @Index(name = "idx_reset_identifier", columnList = "identifier"),
        @Index(name = "idx_reset_expires", columnList = "expiresAt") })
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PasswordResetChannel channel;
    @Column(nullable = false)
    private String identifier;
    @Column(nullable = false)
    private String otpHash;
    @Column(length = 100, unique = true)
    private String resetTokenHash;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    @Column(nullable = false)
    private boolean used;
    @Column(nullable = false)
    private boolean otpVerified;
    @Column(nullable = false)
    private int attempts;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long v) {
        userId = v;
    }

    public PasswordResetChannel getChannel() {
        return channel;
    }

    public void setChannel(PasswordResetChannel v) {
        channel = v;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String v) {
        identifier = v;
    }

    public String getOtpHash() {
        return otpHash;
    }

    public void setOtpHash(String v) {
        otpHash = v;
    }

    public String getResetTokenHash() {
        return resetTokenHash;
    }

    public void setResetTokenHash(String v) {
        resetTokenHash = v;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime v) {
        expiresAt = v;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean v) {
        used = v;
    }

    public boolean isOtpVerified() {
        return otpVerified;
    }

    public void setOtpVerified(boolean v) {
        otpVerified = v;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int v) {
        attempts = v;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
