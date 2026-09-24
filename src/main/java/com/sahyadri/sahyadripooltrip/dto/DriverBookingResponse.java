package com.sahyadri.sahyadripooltrip.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DriverBookingResponse {

    private Long bookingId;
    private Long tripId;

    private String travelerName;
    private String travelerEmail;

    private String fromLocation;
    private String toLocation;

    private LocalDate travelDate;

    private Integer seatsBooked;
    private Double totalAmount;

    private String status;
    private LocalDateTime bookedAt;

    public DriverBookingResponse(
            Long bookingId,
            Long tripId,
            String travelerName,
            String travelerEmail,
            String fromLocation,
            String toLocation,
            LocalDate travelDate,
            Integer seatsBooked,
            Double totalAmount,
            String status,
            LocalDateTime bookedAt) {

        this.bookingId = bookingId;
        this.tripId = tripId;
        this.travelerName = travelerName;
        this.travelerEmail = travelerEmail;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.travelDate = travelDate;
        this.seatsBooked = seatsBooked;
        this.totalAmount = totalAmount;
        this.status = status;
        this.bookedAt = bookedAt;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public Long getTripId() {
        return tripId;
    }

    public String getTravelerName() {
        return travelerName;
    }

    public String getTravelerEmail() {
        return travelerEmail;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public Integer getSeatsBooked() {
        return seatsBooked;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }
}