package com.sahyadri.sahyadripooltrip.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "hotel_owner_applications")
public class HotelOwnerApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    @Column(nullable = false, length = 150)
    private String ownerName;
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    @Column(nullable = false, unique = true, length = 20)
    private String phone;
    @JsonIgnore
    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 255)
    private String propertyName;
    @Column(nullable = false, length = 80)
    private String propertyType;
    @Column(nullable = false, length = 1000)
    private String address;
    @Column(nullable = false, length = 150)
    private String cityDistrict;
    @Column(nullable = false)
    private Integer totalRooms;
    @Column(nullable = false)
    private Double pricePerNight;
    @Column(nullable = false, length = 3000)
    private String description;

    @Column(nullable = false, length = 30)
    private String status = "PENDING";
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    @Column(length = 150)
    private String reviewedBy;

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); }

    public HotelOwnerApplication() {}

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getPropertyName() { return propertyName; }
    public void setPropertyName(String propertyName) { this.propertyName = propertyName; }
    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCityDistrict() { return cityDistrict; }
    public void setCityDistrict(String cityDistrict) { this.cityDistrict = cityDistrict; }
    public Integer getTotalRooms() { return totalRooms; }
    public void setTotalRooms(Integer totalRooms) { this.totalRooms = totalRooms; }
    public Double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(Double pricePerNight) { this.pricePerNight = pricePerNight; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
}
