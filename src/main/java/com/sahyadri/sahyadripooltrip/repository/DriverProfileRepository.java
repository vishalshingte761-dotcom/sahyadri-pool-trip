package com.sahyadri.sahyadripooltrip.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sahyadri.sahyadripooltrip.entity.*;

public interface DriverProfileRepository extends JpaRepository<DriverProfile, Long> {
    Optional<DriverProfile> findByUserId(Long userId);

    List<DriverProfile> findByVerificationStatus(VerificationStatus status);

    long countByVerificationStatus(VerificationStatus status);
}
