package com.sahyadri.sahyadripooltrip.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "complaints")
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complaintId;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private String category;
    @Column(nullable = false)
    private String subject;
    @Column(nullable = false, length = 5000)
    private String description;
    private Long relatedTripId;
    private Long relatedBookingId;
    private Long relatedPropertyId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintPriority priority;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus status;
    private Long handledBy;
    private LocalDateTime handledAt;
    @Column(length = 2000)
    private String adminResponse;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        if (priority == null)
            priority = ComplaintPriority.NORMAL;
        if (status == null)
            status = ComplaintStatus.OPEN;
    }

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long v) {
        complaintId = v;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long v) {
        userId = v;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String v) {
        category = v;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String v) {
        subject = v;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String v) {
        description = v;
    }

    public Long getRelatedTripId() {
        return relatedTripId;
    }

    public void setRelatedTripId(Long v) {
        relatedTripId = v;
    }

    public Long getRelatedBookingId() {
        return relatedBookingId;
    }

    public void setRelatedBookingId(Long v) {
        relatedBookingId = v;
    }

    public Long getRelatedPropertyId() {
        return relatedPropertyId;
    }

    public void setRelatedPropertyId(Long v) {
        relatedPropertyId = v;
    }

    public ComplaintPriority getPriority() {
        return priority;
    }

    public void setPriority(ComplaintPriority v) {
        priority = v;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus v) {
        status = v;
    }

    public Long getHandledBy() {
        return handledBy;
    }

    public void setHandledBy(Long v) {
        handledBy = v;
    }

    public LocalDateTime getHandledAt() {
        return handledAt;
    }

    public void setHandledAt(LocalDateTime v) {
        handledAt = v;
    }

    public String getAdminResponse() {
        return adminResponse;
    }

    public void setAdminResponse(String v) {
        adminResponse = v;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime v) {
        createdAt = v;
    }
}
