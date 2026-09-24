package com.sahyadri.sahyadripooltrip.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fort_routes")
public class FortRoute {

    @Id
    @Column(name = "route_id", nullable = false, unique = true, length = 100)
    private String routeId;

    @Column(name = "fort_id", nullable = false, length = 50)
    private String fortId;

    @Column(name = "route_name", length = 255)
    private String routeName;

    @Column(name = "route_start", length = 255)
    private String routeStart;

    @Column(name = "route_start_latitude")
    private Double routeStartLatitude;

    @Column(name = "route_start_longitude")
    private Double routeStartLongitude;

    @Column(name = "route_end", length = 255)
    private String routeEnd;

    @Column(name = "route_distance_km")
    private Double routeDistanceKm;

    @Column(name = "route_duration", length = 100)
    private String routeDuration;

    @Column(name = "route_difficulty", length = 100)
    private String routeDifficulty;

    @Column(name = "route_type", length = 100)
    private String routeType;

    @Column(name = "trek_start", length = 255)
    private String trekStart;

    @Column(name = "trek_route", length = 2000)
    private String trekRoute;

    @Column(name = "trek_distance_km")
    private Double trekDistanceKm;

    @Column(name = "trek_duration", length = 100)
    private String trekDuration;

    @Column(name = "difficulty", length = 100)
    private String difficulty;

    @Column(name = "best_season", length = 255)
    private String bestSeason;

    @Column(name = "protection_status", length = 255)
    private String protectionStatus;

    @Column(name = "route_source", length = 500)
    private String routeSource;

    @Column(name = "route_verification_status", length = 100)
    private String routeVerificationStatus;

    @Column(name = "gps_status", length = 100)
    private String gpsStatus;

    @Column(name = "trek_status", length = 100)
    private String trekStatus;

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

    @Column(name = "base_village_source", length = 500)
    private String baseVillageSource;

    @Column(name = "trek_start_latitude")
    private Double trekStartLatitude;

    @Column(name = "trek_start_longitude")
    private Double trekStartLongitude;

    @Column(name = "route_confidence", length = 50)
    private String routeConfidence;

    public FortRoute() {
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getFortId() {
        return fortId;
    }

    public void setFortId(String fortId) {
        this.fortId = fortId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRouteStart() {
        return routeStart;
    }

    public void setRouteStart(String routeStart) {
        this.routeStart = routeStart;
    }

    public Double getRouteStartLatitude() {
        return routeStartLatitude;
    }

    public void setRouteStartLatitude(Double routeStartLatitude) {
        this.routeStartLatitude = routeStartLatitude;
    }

    public Double getRouteStartLongitude() {
        return routeStartLongitude;
    }

    public void setRouteStartLongitude(Double routeStartLongitude) {
        this.routeStartLongitude = routeStartLongitude;
    }

    public String getRouteEnd() {
        return routeEnd;
    }

    public void setRouteEnd(String routeEnd) {
        this.routeEnd = routeEnd;
    }

    public Double getRouteDistanceKm() {
        return routeDistanceKm;
    }

    public void setRouteDistanceKm(Double routeDistanceKm) {
        this.routeDistanceKm = routeDistanceKm;
    }

    public String getRouteDuration() {
        return routeDuration;
    }

    public void setRouteDuration(String routeDuration) {
        this.routeDuration = routeDuration;
    }

    public String getRouteDifficulty() {
        return routeDifficulty;
    }

    public void setRouteDifficulty(String routeDifficulty) {
        this.routeDifficulty = routeDifficulty;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public String getTrekStart() {
        return trekStart;
    }

    public void setTrekStart(String trekStart) {
        this.trekStart = trekStart;
    }

    public String getTrekRoute() {
        return trekRoute;
    }

    public void setTrekRoute(String trekRoute) {
        this.trekRoute = trekRoute;
    }

    public Double getTrekDistanceKm() {
        return trekDistanceKm;
    }

    public void setTrekDistanceKm(Double trekDistanceKm) {
        this.trekDistanceKm = trekDistanceKm;
    }

    public String getTrekDuration() {
        return trekDuration;
    }

    public void setTrekDuration(String trekDuration) {
        this.trekDuration = trekDuration;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getBestSeason() {
        return bestSeason;
    }

    public void setBestSeason(String bestSeason) {
        this.bestSeason = bestSeason;
    }

    public String getProtectionStatus() {
        return protectionStatus;
    }

    public void setProtectionStatus(String protectionStatus) {
        this.protectionStatus = protectionStatus;
    }

    public String getRouteSource() {
        return routeSource;
    }

    public void setRouteSource(String routeSource) {
        this.routeSource = routeSource;
    }

    public String getRouteVerificationStatus() {
        return routeVerificationStatus;
    }

    public void setRouteVerificationStatus(String routeVerificationStatus) {
        this.routeVerificationStatus = routeVerificationStatus;
    }

    public String getGpsStatus() {
        return gpsStatus;
    }

    public void setGpsStatus(String gpsStatus) {
        this.gpsStatus = gpsStatus;
    }

    public String getTrekStatus() {
        return trekStatus;
    }

    public void setTrekStatus(String trekStatus) {
        this.trekStatus = trekStatus;
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

    public String getBaseVillageSource() {
        return baseVillageSource;
    }

    public void setBaseVillageSource(String baseVillageSource) {
        this.baseVillageSource = baseVillageSource;
    }

    public Double getTrekStartLatitude() {
        return trekStartLatitude;
    }

    public void setTrekStartLatitude(Double trekStartLatitude) {
        this.trekStartLatitude = trekStartLatitude;
    }

    public Double getTrekStartLongitude() {
        return trekStartLongitude;
    }

    public void setTrekStartLongitude(Double trekStartLongitude) {
        this.trekStartLongitude = trekStartLongitude;
    }

    public String getRouteConfidence() {
        return routeConfidence;
    }

    public void setRouteConfidence(String routeConfidence) {
        this.routeConfidence = routeConfidence;
    }
}