package com.sahyadri.sahyadripooltrip.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sahyadri.sahyadripooltrip.entity.*;

public interface AgencyRepository extends JpaRepository<Agency, Long> {
    Optional<Agency> findByOwnerUserId(Long userId);

    List<Agency> findByVerificationStatus(VerificationStatus status);

    long countByVerificationStatus(VerificationStatus status);
}
