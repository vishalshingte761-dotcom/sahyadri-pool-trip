package com.sahyadri.sahyadripooltrip.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tripId;

    @Column(nullable = false)
    private String fromLocation;

    @Column(nullable = false)
    private String toLocation;

    @Column(nullable = false)
    private LocalDate travelDate;

    @Column(nullable = false)
    private LocalTime departureTime;

    @Column(nullable = false)
    private Integer totalSeats;

    @Column(nullable = false)
    private Integer availableSeats;

    @Column(nullable = false)
    private Double pricePerSeat;

    @Column(length = 80)
    private String vehicleType;

    private Integer seatingCapacity;

    private Double distanceKm;

    @Column(nullable = false)
    private Boolean returnTrip = false;

    private Double baseTripFare;
    private Double platformFee;
    private Double serviceFee;
    private Double totalTripFare;

    @jakarta.persistence.Transient
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Double vehicleRatePerKm;

    @Column(nullable = true)
    private Long driverId;

    @Column(nullable = true)
    private Long agencyId;

    @Column(nullable = true)
    private Long agencyVehicleId;

    @Column(length = 20)
    private String bookingMode = "SEAT_BASED";

    @Column(length = 2000)
    private String pickupPoints;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();

        if (availableSeats == null) {
            availableSeats = totalSeats;
        }

        if (status == null) {
            status = "ACTIVE";
        }
    }

    public Trip() {
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Double getPricePerSeat() {
        return pricePerSeat;
    }

    public void setPricePerSeat(Double pricePerSeat) {
        this.pricePerSeat = pricePerSeat;
    }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String v) { vehicleType = v; }
    public Integer getSeatingCapacity() { return seatingCapacity; }
    public void setSeatingCapacity(Integer v) { seatingCapacity = v; }
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double v) { distanceKm = v; }
    public Boolean getReturnTrip() { return returnTrip; }
    public void setReturnTrip(Boolean v) { returnTrip = v; }
    public Double getBaseTripFare() { return baseTripFare; }
    public void setBaseTripFare(Double v) { baseTripFare = v; }
    public Double getPlatformFee() { return platformFee; }
    public void setPlatformFee(Double v) { platformFee = v; }
    public Double getServiceFee() { return serviceFee; }
    public void setServiceFee(Double v) { serviceFee = v; }
    public Double getTotalTripFare() { return totalTripFare; }
    public void setTotalTripFare(Double v) { totalTripFare = v; }
    public Double getVehicleRatePerKm() { return vehicleRatePerKm; }
    public void setVehicleRatePerKm(Double v) { vehicleRatePerKm = v; }

    public Long getAgencyId() { return agencyId; }
    public void setAgencyId(Long agencyId) { this.agencyId = agencyId; }

    public Long getAgencyVehicleId() { return agencyVehicleId; }
    public void setAgencyVehicleId(Long agencyVehicleId) { this.agencyVehicleId = agencyVehicleId; }

    public String getBookingMode() { return bookingMode; }
    public void setBookingMode(String bookingMode) { this.bookingMode = bookingMode; }

    public String getPickupPoints() { return pickupPoints; }
    public void setPickupPoints(String pickupPoints) { this.pickupPoints = pickupPoints; }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}