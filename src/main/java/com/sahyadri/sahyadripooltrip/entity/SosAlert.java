package com.sahyadri.sahyadripooltrip.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "sos_alerts")
public class SosAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sosId;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private LocalDateTime triggeredAt;
    private Double latitude;
    private Double longitude;
    private Double accuracy;
    @Column(nullable = false)
    private String emergencyContactName;
    @Column(nullable = false)
    private String emergencyContactNumber;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SosStatus status;
    private LocalDateTime resolvedAt;
    @Column(length = 1000)
    private String resolutionNote;

    @PrePersist
    void onCreate() {
        if (triggeredAt == null)
            triggeredAt = LocalDateTime.now();
        if (status == null)
            status = SosStatus.ACTIVE;
    }

    public Long getSosId() {
        return sosId;
    }

    public void setSosId(Long v) {
        sosId = v;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long v) {
        userId = v;
    }

    public LocalDateTime getTriggeredAt() {
        return triggeredAt;
    }

    public void setTriggeredAt(LocalDateTime v) {
        triggeredAt = v;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double v) {
        latitude = v;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double v) {
        longitude = v;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double v) {
        accuracy = v;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String v) {
        emergencyContactName = v;
    }

    public String getEmergencyContactNumber() {
        return emergencyContactNumber;
    }

    public void setEmergencyContactNumber(String v) {
        emergencyContactNumber = v;
    }

    public SosStatus getStatus() {
        return status;
    }

    public void setStatus(SosStatus v) {
        status = v;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime v) {
        resolvedAt = v;
    }

    public String getResolutionNote() {
        return resolutionNote;
    }

    public void setResolutionNote(String v) {
        resolutionNote = v;
    }
}
