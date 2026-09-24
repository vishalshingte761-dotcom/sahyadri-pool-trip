package com.sahyadri.sahyadripooltrip.service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sahyadri.sahyadripooltrip.entity.PasswordResetChannel;
import com.sahyadri.sahyadripooltrip.entity.PasswordResetToken;
import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.repository.PasswordResetTokenRepository;
import com.sahyadri.sahyadripooltrip.repository.UserRepository;

@Service
public class PasswordResetService {

    private final UserRepository users;
    private final PasswordResetTokenRepository tokens;
    private final BCryptPasswordEncoder encoder;
    private final NotificationService notifications;

    // OTP security settings
    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int OTP_MAX_ATTEMPTS = 5;
    private static final int OTP_RESEND_COOLDOWN_SECONDS = 60;
    private static final int OTP_MAX_REQUESTS = 3;
    private static final int OTP_REQUEST_WINDOW_MINUTES = 15;

    public PasswordResetService(
            UserRepository u,
            PasswordResetTokenRepository t,
            BCryptPasswordEncoder e,
            NotificationService n) {

        users = u;
        tokens = t;
        encoder = e;
        notifications = n;
    }

    /*
     * =========================================================
     * EMAIL OTP
     * =========================================================
     */
    public void startEmail(String email) {

        String id = email.trim();

        User u = users.findByEmail(id).orElse(null);

        // Do not reveal whether account exists.
        if (u == null) {
            return;
        }

        checkOtpRequestLimit(id);

        issue(u, PasswordResetChannel.EMAIL, id);
    }

    /*
     * =========================================================
     * PHONE OTP
     * =========================================================
     */
    public void startPhone(String phone) {

        String id = phone.trim();

        User u = users.findByPhone(id).orElse(null);

        // Do not reveal whether account exists.
        if (u == null) {
            return;
        }

        checkOtpRequestLimit(id);

        issue(u, PasswordResetChannel.PHONE, id);
    }

    /*
     * =========================================================
     * OTP REQUEST RATE LIMIT
     * =========================================================
     */
    private void checkOtpRequestLimit(String identifier) {

        LocalDateTime now = LocalDateTime.now();

        var recentTokens = tokens.findAll()
                .stream()
                .filter(x -> identifier.equalsIgnoreCase(x.getIdentifier()))
                .filter(x -> x.getCreatedAt() != null)
                .filter(x -> x.getCreatedAt()
                        .isAfter(now.minusMinutes(OTP_REQUEST_WINDOW_MINUTES)))
                .toList();

        /*
         * Maximum 3 OTP requests in 15 minutes.
         */
        if (recentTokens.size() >= OTP_MAX_REQUESTS) {
            throw new IllegalArgumentException(
                    "Too many OTP requests. Please try again later.");
        }

        /*
         * Minimum 60 seconds between OTP requests.
         */
        if (!recentTokens.isEmpty()) {

            PasswordResetToken latest = recentTokens.stream()
                    .max((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                    .orElse(null);

            if (latest != null) {

                long seconds = java.time.Duration.between(
                        latest.getCreatedAt(),
                        now).getSeconds();

                if (seconds < OTP_RESEND_COOLDOWN_SECONDS) {
                    long remaining = OTP_RESEND_COOLDOWN_SECONDS - seconds;

                    throw new IllegalArgumentException(
                            "Please wait " + remaining
                                    + " seconds before requesting another OTP.");
                }
            }
        }
    }

    /*
     * =========================================================
     * CREATE OTP
     * =========================================================
     */
    private void issue(
            User u,
            PasswordResetChannel channel,
            String id) {

        /*
         * Invalidate previous active OTP.
         */
        tokens.findTopByIdentifierAndUsedFalseOrderByCreatedAtDesc(id)
                .ifPresent(x -> {
                    x.setUsed(true);
                    tokens.save(x);
                });

        String otp = String.format(
                "%06d",
                ThreadLocalRandom.current().nextInt(0, 1000000));

        PasswordResetToken t = new PasswordResetToken();

        t.setUserId(u.getUserId());
        t.setChannel(channel);
        t.setIdentifier(id);
        t.setOtpHash(encoder.encode(otp));
        t.setExpiresAt(
                LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));

        t.setUsed(false);
        t.setOtpVerified(false);
        t.setAttempts(0);

        tokens.save(t);

        if (channel == PasswordResetChannel.EMAIL) {
            notifications.sendEmailOtp(id, otp);
        } else {
            notifications.sendSmsOtp(id, otp);
        }
    }

    /*
     * =========================================================
     * VERIFY OTP
     *
     * IMPORTANT:
     * No @Transactional here.
     *
     * This allows failed-attempt counter to be committed
     * before IllegalArgumentException is thrown.
     * =========================================================
     */
    public String verify(String identifier, String otp) {

        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException(
                    "Identifier is required");
        }

        if (otp == null || otp.isBlank()) {
            throw new IllegalArgumentException(
                    "OTP is required");
        }

        String id = identifier.trim();

        PasswordResetToken t = tokens.findTopByIdentifierAndUsedFalseOrderByCreatedAtDesc(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "OTP not found or expired"));

        /*
         * OTP already verified.
         */
        if (t.isOtpVerified()) {
            throw new IllegalArgumentException(
                    "OTP has already been verified");
        }

        /*
         * Expired OTP.
         */
        if (t.getExpiresAt().isBefore(LocalDateTime.now())) {

            t.setUsed(true);
            tokens.save(t);

            throw new IllegalArgumentException(
                    "OTP has expired");
        }

        /*
         * Maximum 5 incorrect attempts.
         */
        if (t.getAttempts() >= OTP_MAX_ATTEMPTS) {

            t.setUsed(true);
            tokens.save(t);

            throw new IllegalArgumentException(
                    "Too many incorrect OTP attempts. Please request a new OTP.");
        }

        /*
         * Increment BEFORE checking OTP.
         *
         * IMPORTANT:
         * tokens.save() commits because verify() is not
         * wrapped inside one big transaction.
         */
        t.setAttempts(t.getAttempts() + 1);

        /*
         * Wrong OTP.
         */
        if (!encoder.matches(otp.trim(), t.getOtpHash())) {

            /*
             * Lock token immediately on 5th wrong attempt.
             */
            if (t.getAttempts() >= OTP_MAX_ATTEMPTS) {
                t.setUsed(true);
                tokens.save(t);

                throw new IllegalArgumentException(
                        "Too many incorrect OTP attempts. Please request a new OTP.");
            }

            tokens.save(t);

            throw new IllegalArgumentException(
                    "Invalid OTP");
        }

        /*
         * Correct OTP.
         */
        String raw = UUID.randomUUID().toString();

        t.setOtpVerified(true);
        t.setResetTokenHash(encoder.encode(raw));

        /*
         * DO NOT set used=true here.
         *
         * The same record is required later by confirm()
         * to validate the reset token.
         */
        tokens.save(t);

        return raw;
    }

    /*
     * =========================================================
     * RESET PASSWORD
     * =========================================================
     */
    public void confirm(
            String resetToken,
            String newPassword) {

        if (resetToken == null || resetToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Reset token is required");
        }

        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters");
        }

        PasswordResetToken found = tokens.findAll()
                .stream()
                .filter(x -> !x.isUsed())
                .filter(PasswordResetToken::isOtpVerified)
                .filter(x -> x.getResetTokenHash() != null)
                .filter(x -> encoder.matches(
                        resetToken,
                        x.getResetTokenHash()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Reset token is invalid or expired"));

        /*
         * Reset token lifetime:
         * OTP expiry + 10 minutes.
         */
        if (found.getExpiresAt()
                .plusMinutes(10)
                .isBefore(LocalDateTime.now())) {

            found.setUsed(true);
            tokens.save(found);

            throw new IllegalArgumentException(
                    "Reset token has expired");
        }

        User u = users.findById(found.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found"));

        u.setPasswordHash(
                encoder.encode(newPassword));

        users.save(u);

        /*
         * Single-use reset token.
         */
        found.setUsed(true);
        tokens.save(found);
    }
}