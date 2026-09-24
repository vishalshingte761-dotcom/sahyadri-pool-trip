package com.sahyadri.sahyadripooltrip.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.sahyadri.sahyadripooltrip.entity.*;
import com.sahyadri.sahyadripooltrip.repository.*;

@Service
public class VerificationService {
    private final DriverProfileRepository drivers;
    private final AgencyRepository agencies;

    public VerificationService(DriverProfileRepository drivers, AgencyRepository agencies) {
        this.drivers = drivers;
        this.agencies = agencies;
    }

    public void requireApprovedDriver(Long userId) {
        DriverProfile p = drivers.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Driver profile is incomplete. Complete document verification before creating trips."));
        if (p.getVerificationStatus() != VerificationStatus.APPROVED)
            throw new IllegalArgumentException(
                    "Driver verification is pending. Admin approval is required before creating trips.");
    }

    public DriverProfile reviewDriver(Long id, VerificationStatus status, String reason, Long adminId) {
        DriverProfile p = drivers.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Driver profile not found"));
        p.setVerificationStatus(status);
        p.setRejectionReason(reason);
        p.setReviewedBy(adminId);
        p.setReviewedAt(LocalDateTime.now());
        return drivers.save(p);
    }

    public Agency reviewAgency(Long id, VerificationStatus status, String reason, Long adminId) {
        Agency a = agencies.findById(id).orElseThrow(() -> new IllegalArgumentException("Agency not found"));
        a.setVerificationStatus(status);
        a.setRejectionReason(reason);
        a.setReviewedBy(adminId);
        a.setReviewedAt(LocalDateTime.now());
        return agencies.save(a);
    }
}
