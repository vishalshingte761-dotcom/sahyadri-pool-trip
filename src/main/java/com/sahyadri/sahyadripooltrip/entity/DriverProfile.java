package com.sahyadri.sahyadripooltrip.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "driver_profiles")
public class DriverProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long driverProfileId;
    @Column(nullable = false, unique = true)
    private Long userId;
    @Column(nullable = false)
    private String fullName;
    private LocalDate dateOfBirth;
    @Column(length = 1000)
    private String address;
    @Column(nullable = false)
    private String emergencyContactName;
    @Column(nullable = false)
    private String emergencyContactNumber;
    @Column(nullable = false)
    private String emergencyContactRelation;
    @Column(nullable = false)
    private String aadhaarDocument;
    @Column(nullable = false)
    private String drivingLicenceDocument;
    private String panDocument;
    private String addressProofDocument;
    @Column(nullable = false)
    private String vehicleRegistrationNumber;
    @Column(nullable = false)
    private String vehicleType;
    @Column(nullable = false)
    private Integer seatingCapacity;
    private String vehiclePhoto;
    @Column(nullable = false)
    private String rcDocument;
    @Column(nullable = false)
    private String insuranceDocument;
    @Column(nullable = false)
    private String pucDocument;
    private String commercialPermitDocument;
    private String fitnessCertificateDocument;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus verificationStatus;
    @Column(length = 1000)
    private String rejectionReason;
    private Long reviewedBy;
    private LocalDateTime reviewedAt;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        if (verificationStatus == null)
            verificationStatus = VerificationStatus.PENDING;
    }

    public Long getDriverProfileId() {
        return driverProfileId;
    }

    public void setDriverProfileId(Long v) {
        driverProfileId = v;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long v) {
        userId = v;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String v) {
        fullName = v;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate v) {
        dateOfBirth = v;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String v) {
        address = v;
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

    public String getEmergencyContactRelation() {
        return emergencyContactRelation;
    }

    public void setEmergencyContactRelation(String v) {
        emergencyContactRelation = v;
    }

    public String getAadhaarDocument() {
        return aadhaarDocument;
    }

    public void setAadhaarDocument(String v) {
        aadhaarDocument = v;
    }

    public String getDrivingLicenceDocument() {
        return drivingLicenceDocument;
    }

    public void setDrivingLicenceDocument(String v) {
        drivingLicenceDocument = v;
    }

    public String getPanDocument() {
        return panDocument;
    }

    public void setPanDocument(String v) {
        panDocument = v;
    }

    public String getAddressProofDocument() {
        return addressProofDocument;
    }

    public void setAddressProofDocument(String v) {
        addressProofDocument = v;
    }

    public String getVehicleRegistrationNumber() {
        return vehicleRegistrationNumber;
    }

    public void setVehicleRegistrationNumber(String v) {
        vehicleRegistrationNumber = v;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String v) {
        vehicleType = v;
    }

    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(Integer v) {
        seatingCapacity = v;
    }

    public String getVehiclePhoto() {
        return vehiclePhoto;
    }

    public void setVehiclePhoto(String v) {
        vehiclePhoto = v;
    }

    public String getRcDocument() {
        return rcDocument;
    }

    public void setRcDocument(String v) {
        rcDocument = v;
    }

    public String getInsuranceDocument() {
        return insuranceDocument;
    }

    public void setInsuranceDocument(String v) {
        insuranceDocument = v;
    }

    public String getPucDocument() {
        return pucDocument;
    }

    public void setPucDocument(String v) {
        pucDocument = v;
    }

    public String getCommercialPermitDocument() {
        return commercialPermitDocument;
    }

    public void setCommercialPermitDocument(String v) {
        commercialPermitDocument = v;
    }

    public String getFitnessCertificateDocument() {
        return fitnessCertificateDocument;
    }

    public void setFitnessCertificateDocument(String v) {
        fitnessCertificateDocument = v;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus v) {
        verificationStatus = v;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String v) {
        rejectionReason = v;
    }

    public Long getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Long v) {
        reviewedBy = v;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime v) {
        reviewedAt = v;
    }

    public Long getReviewedById() {
        return reviewedBy;
    }

    public LocalDateTime getReviewedAtValue() {
        return reviewedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime v) {
        createdAt = v;
    }
}
