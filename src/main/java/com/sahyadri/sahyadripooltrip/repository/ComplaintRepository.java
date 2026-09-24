package com.sahyadri.sahyadripooltrip.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sahyadri.sahyadripooltrip.entity.*;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Complaint> findAllByOrderByCreatedAtDesc();

    long countByStatus(ComplaintStatus status);
}
