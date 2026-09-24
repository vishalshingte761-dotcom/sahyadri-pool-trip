package com.sahyadri.sahyadripooltrip.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TravelerBookingResponse {

    private Long bookingId;
    private Long tripId;

    private String driverName;
    private String driverEmail;
    private String driverPhone;

    private String fromLocation;
    private String toLocation;

    private LocalDate travelDate;
    private LocalTime departureTime;

    private Integer seatsBooked;
    private Double totalAmount;

    private String status;
    private LocalDateTime bookedAt;

    public TravelerBookingResponse(
            Long bookingId,
            Long tripId,
            String driverName,
            String driverEmail,
            String driverPhone,
            String fromLocation,
            String toLocation,
            LocalDate travelDate,
            LocalTime departureTime,
            Integer seatsBooked,
            Double totalAmount,
            String status,
            LocalDateTime bookedAt) {

        this.bookingId = bookingId;
        this.tripId = tripId;
        this.driverName = driverName;
        this.driverEmail = driverEmail;
        this.driverPhone = driverPhone;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.travelDate = travelDate;
        this.departureTime = departureTime;
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

    public String getDriverName() {
        return driverName;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public String getDriverPhone() {
        return driverPhone;
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

    public LocalTime getDepartureTime() {
        return departureTime;
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