package com.sahyadri.sahyadripooltrip.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sahyadri.sahyadripooltrip.entity.SosAlert;
import com.sahyadri.sahyadripooltrip.entity.SosStatus;

public interface SosAlertRepository extends JpaRepository<SosAlert, Long> {

    List<SosAlert> findByUserIdOrderByTriggeredAtDesc(Long userId);

    List<SosAlert> findByStatusOrderByTriggeredAtDesc(SosStatus status);

    long countByStatus(SosStatus status);

    Optional<SosAlert> findFirstByUserIdAndStatusOrderByTriggeredAtDesc(
            Long userId,
            SosStatus status);
}