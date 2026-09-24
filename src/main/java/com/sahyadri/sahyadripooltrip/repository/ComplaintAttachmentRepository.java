package com.sahyadri.sahyadripooltrip.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sahyadri.sahyadripooltrip.entity.ComplaintAttachment;

public interface ComplaintAttachmentRepository extends JpaRepository<ComplaintAttachment, Long> {
    List<ComplaintAttachment> findByComplaintId(Long complaintId);
}
