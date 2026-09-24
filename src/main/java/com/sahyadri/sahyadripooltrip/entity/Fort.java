package com.sahyadri.sahyadripooltrip.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "forts")
public class Fort {

    @Id
    @Column(name = "fort_id", nullable = false, unique = true, length = 50)
    private String fortId;

    @Column(name = "fort_name", nullable = false, length = 255)
    private String fortName;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "locality", length = 255)
    private String locality;

    @Column(name = "base_village", length = 255)
    private String baseVillage;

    @Column(name = "base_village_source", length = 500)
    private String baseVillageSource;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "location_source", length = 500)
    private String locationSource;

    @Column(name = "location_confidence", length = 500)
    private String locationConfidence;

    @Column(name = "fort_type", length = 100)
    private String fortType;

    @Column(name = "elevation_m")
    private Double elevationM;

    @Column(name = "best_season", length = 255)
    private String bestSeason;

    @Column(name = "monsoon_risk", length = 255)
    private String monsoonRisk;

    @Column(name = "parking", length = 255)
    private String parking;

    @Column(name = "water_availability", length = 255)
    private String waterAvailability;

    @Column(name = "network_availability", length = 255)
    private String networkAvailability;

    @Column(name = "protection_status", length = 255)
    private String protectionStatus;

    @Column(name = "primary_source", length = 500)
    private String primarySource;

    @Column(name = "verification_status", length = 100)
    private String verificationStatus;

    @Column(name = "location_verified_date", length = 50)
    private String locationVerifiedDate;

    @Column(name = "last_verified", length = 50)
    private String lastVerified;

    @Column(name = "dataset_scope", length = 100)
    private String datasetScope;

    @Column(name = "record_status", length = 100)
    private String recordStatus;

    public Fort() {
    }

    public String getFortId() {
        return fortId;
    }

    public void setFortId(String fortId) {
        this.fortId = fortId;
    }

    public String getFortName() {
        return fortName;
    }

    public void setFortName(String fortName) {
        this.fortName = fortName;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getBaseVillage() {
        return baseVillage;
    }

    public void setBaseVillage(String baseVillage) {
        this.baseVillage = baseVillage;
    }

    public String getBaseVillageSource() {
        return baseVillageSource;
    }

    public void setBaseVillageSource(String baseVillageSource) {
        this.baseVillageSource = baseVillageSource;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getLocationSource() {
        return locationSource;
    }

    public void setLocationSource(String locationSource) {
        this.locationSource = locationSource;
    }

    public String getLocationConfidence() {
        return locationConfidence;
    }

    public void setLocationConfidence(String locationConfidence) {
        this.locationConfidence = locationConfidence;
    }

    public String getFortType() {
        return fortType;
    }

    public void setFortType(String fortType) {
        this.fortType = fortType;
    }

    public Double getElevationM() {
        return elevationM;
    }

    public void setElevationM(Double elevationM) {
        this.elevationM = elevationM;
    }

    public String getBestSeason() {
        return bestSeason;
    }

    public void setBestSeason(String bestSeason) {
        this.bestSeason = bestSeason;
    }

    public String getMonsoonRisk() {
        return monsoonRisk;
    }

    public void setMonsoonRisk(String monsoonRisk) {
        this.monsoonRisk = monsoonRisk;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public String getWaterAvailability() {
        return waterAvailability;
    }

    public void setWaterAvailability(String waterAvailability) {
        this.waterAvailability = waterAvailability;
    }

    public String getNetworkAvailability() {
        return networkAvailability;
    }

    public void setNetworkAvailability(String networkAvailability) {
        this.networkAvailability = networkAvailability;
    }

    public String getProtectionStatus() {
        return protectionStatus;
    }

    public void setProtectionStatus(String protectionStatus) {
        this.protectionStatus = protectionStatus;
    }

    public String getPrimarySource() {
        return primarySource;
    }

    public void setPrimarySource(String primarySource) {
        this.primarySource = primarySource;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getLocationVerifiedDate() {
        return locationVerifiedDate;
    }

    public void setLocationVerifiedDate(String locationVerifiedDate) {
        this.locationVerifiedDate = locationVerifiedDate;
    }

    public String getLastVerified() {
        return lastVerified;
    }

    public void setLastVerified(String lastVerified) {
        this.lastVerified = lastVerified;
    }

    public String getDatasetScope() {
        return datasetScope;
    }

    public void setDatasetScope(String datasetScope) {
        this.datasetScope = datasetScope;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }
}