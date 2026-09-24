package com.sahyadri.sahyadripooltrip.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public class CreateAgencyTripRequest {
    @NotBlank private String fromLocation;
    @NotBlank private String toLocation;
    @NotNull @FutureOrPresent private LocalDate travelDate;
    @NotNull private LocalTime departureTime;
    @NotNull @Positive private Long agencyVehicleId;
    @NotNull @Min(1) private Integer publishedSeats;
    @NotNull @Positive private Double distanceKm;
    private Boolean returnTrip = false;
    private String bookingMode = "SEAT_BASED";
    private String pickupPoints;

    public String getFromLocation() { return fromLocation; }
    public void setFromLocation(String v) { fromLocation = v; }
    public String getToLocation() { return toLocation; }
    public void setToLocation(String v) { toLocation = v; }
    public LocalDate getTravelDate() { return travelDate; }
    public void setTravelDate(LocalDate v) { travelDate = v; }
    public LocalTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalTime v) { departureTime = v; }
    public Long getAgencyVehicleId() { return agencyVehicleId; }
    public void setAgencyVehicleId(Long v) { agencyVehicleId = v; }
    public Integer getPublishedSeats() { return publishedSeats; }
    public void setPublishedSeats(Integer v) { publishedSeats = v; }
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double v) { distanceKm = v; }
    public Boolean getReturnTrip() { return returnTrip; }
    public void setReturnTrip(Boolean v) { returnTrip = v; }
    public String getBookingMode() { return bookingMode; }
    public void setBookingMode(String v) { bookingMode = v; }
    public String getPickupPoints() { return pickupPoints; }
    public void setPickupPoints(String v) { pickupPoints = v; }
}
