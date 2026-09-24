package com.sahyadri.sahyadripooltrip.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sahyadri.sahyadripooltrip.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findTopByIdentifierAndUsedFalseOrderByCreatedAtDesc(String identifier);
}
