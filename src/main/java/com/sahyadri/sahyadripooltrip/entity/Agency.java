package com.sahyadri.sahyadripooltrip.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "agencies")
public class Agency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agencyId;
    @Column(nullable = false, unique = true)
    private Long ownerUserId;
    @Column(nullable = false)
    private String businessName;
    @Column(nullable = false)
    private String ownerName;
    @Column(nullable = false)
    private String mobile;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false, length = 1000)
    private String businessAddress;
    @Column(nullable = false)
    private String cityDistrict;
    private String agencyType;
    @Column(nullable = false)
    private String aadhaarDocument;
    @Column(nullable = false)
    private String panUdyamDocument;
    @Column(nullable = false)
    private String bankPassbookDocument;
    @Column(nullable = false)
    private String ownerIdDocument;
    @Column(nullable = false)
    private String shopActDocument;
    private String gstCertificate;
    private String mtdcTourismCertificate;
    private String partnershipOrIncorporationCertificate;
    private String commercialVehicleDocuments;
    private String iataIrctcLicense;
    private String agencyLogo;
    private Integer numberOfVehicles;
    private Integer numberOfDrivers;
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

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long v) {
        agencyId = v;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long v) {
        ownerUserId = v;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String v) {
        businessName = v;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String v) {
        ownerName = v;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String v) {
        mobile = v;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String v) {
        email = v;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String v) {
        businessAddress = v;
    }

    public String getCityDistrict() {
        return cityDistrict;
    }

    public void setCityDistrict(String v) {
        cityDistrict = v;
    }

    public String getAgencyType() {
        return agencyType;
    }

    public void setAgencyType(String v) {
        agencyType = v;
    }

    public String getAadhaarDocument() {
        return aadhaarDocument;
    }

    public void setAadhaarDocument(String v) {
        aadhaarDocument = v;
    }

    public String getPanUdyamDocument() {
        return panUdyamDocument;
    }

    public void setPanUdyamDocument(String v) {
        panUdyamDocument = v;
    }

    public String getBankPassbookDocument() {
        return bankPassbookDocument;
    }

    public void setBankPassbookDocument(String v) {
        bankPassbookDocument = v;
    }

    public String getOwnerIdDocument() {
        return ownerIdDocument;
    }

    public void setOwnerIdDocument(String v) {
        ownerIdDocument = v;
    }

    public String getShopActDocument() {
        return shopActDocument;
    }

    public void setShopActDocument(String v) {
        shopActDocument = v;
    }

    public String getGstCertificate() {
        return gstCertificate;
    }

    public void setGstCertificate(String v) {
        gstCertificate = v;
    }

    public String getMtdcTourismCertificate() {
        return mtdcTourismCertificate;
    }

    public void setMtdcTourismCertificate(String v) {
        mtdcTourismCertificate = v;
    }

    public String getPartnershipOrIncorporationCertificate() {
        return partnershipOrIncorporationCertificate;
    }

    public void setPartnershipOrIncorporationCertificate(String v) {
        partnershipOrIncorporationCertificate = v;
    }

    public String getCommercialVehicleDocuments() {
        return commercialVehicleDocuments;
    }

    public void setCommercialVehicleDocuments(String v) {
        commercialVehicleDocuments = v;
    }

    public String getIataIrctcLicense() {
        return iataIrctcLicense;
    }

    public void setIataIrctcLicense(String v) {
        iataIrctcLicense = v;
    }

    public String getAgencyLogo() {
        return agencyLogo;
    }

    public void setAgencyLogo(String v) {
        agencyLogo = v;
    }

    public Integer getNumberOfVehicles() {
        return numberOfVehicles;
    }

    public void setNumberOfVehicles(Integer v) {
        numberOfVehicles = v;
    }

    public Integer getNumberOfDrivers() {
        return numberOfDrivers;
    }

    public void setNumberOfDrivers(Integer v) {
        numberOfDrivers = v;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime v) {
        createdAt = v;
    }
}
