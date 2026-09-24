package com.sahyadri.sahyadripooltrip.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "complaint_attachments")
public class ComplaintAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachmentId;
    @Column(nullable = false)
    private Long complaintId;
    @Column(nullable = false)
    private String originalFileName;
    @Column(nullable = false, unique = true)
    private String storedFileName;
    @Column(nullable = false)
    private String contentType;
    @Column(nullable = false)
    private Long fileSize;
    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long v) {
        attachmentId = v;
    }

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long v) {
        complaintId = v;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String v) {
        originalFileName = v;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(String v) {
        storedFileName = v;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String v) {
        contentType = v;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long v) {
        fileSize = v;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime v) {
        uploadedAt = v;
    }
}
