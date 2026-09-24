package com.sahyadri.sahyadripooltrip.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "spots")
public class Spot {
    @Id
    @Column(name = "spot_id", nullable = false, unique = true, length = 50)
    private String spotId;
    @Column(nullable = false, length = 255) private String name;
    @Column(length = 50) private String category;
    @Column(length = 100) private String district;
    @Column(length = 255) private String locality;
    private Double latitude;
    private Double longitude;
    @Column(name = "elevation_m") private Double elevationM;
    @Column(name = "height_m") private Double heightM;
    @Column(name = "best_season", length = 255) private String bestSeason;
    @Column(length = 500) private String difficulty;
    @Column(name = "popularity_tier", length = 50) private String popularityTier;
    @Column(length = 2000) private String highlights;
    @Column(name = "primary_source", length = 500) private String primarySource;
    @Column(name = "verification_status", length = 500) private String verificationStatus;
    @Column(name = "last_verified", length = 50) private String lastVerified;
    @Column(name = "record_status", length = 100) private String recordStatus;

    public Spot() {}
    public String getSpotId(){return spotId;} public void setSpotId(String v){spotId=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getCategory(){return category;} public void setCategory(String v){category=v;}
    public String getDistrict(){return district;} public void setDistrict(String v){district=v;}
    public String getLocality(){return locality;} public void setLocality(String v){locality=v;}
    public Double getLatitude(){return latitude;} public void setLatitude(Double v){latitude=v;}
    public Double getLongitude(){return longitude;} public void setLongitude(Double v){longitude=v;}
    public Double getElevationM(){return elevationM;} public void setElevationM(Double v){elevationM=v;}
    public Double getHeightM(){return heightM;} public void setHeightM(Double v){heightM=v;}
    public String getBestSeason(){return bestSeason;} public void setBestSeason(String v){bestSeason=v;}
    public String getDifficulty(){return difficulty;} public void setDifficulty(String v){difficulty=v;}
    public String getPopularityTier(){return popularityTier;} public void setPopularityTier(String v){popularityTier=v;}
    public String getHighlights(){return highlights;} public void setHighlights(String v){highlights=v;}
    public String getPrimarySource(){return primarySource;} public void setPrimarySource(String v){primarySource=v;}
    public String getVerificationStatus(){return verificationStatus;} public void setVerificationStatus(String v){verificationStatus=v;}
    public String getLastVerified(){return lastVerified;} public void setLastVerified(String v){lastVerified=v;}
    public String getRecordStatus(){return recordStatus;} public void setRecordStatus(String v){recordStatus=v;}
}
