package com.sahyadri.sahyadripooltrip.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateBookingRequest {

    @NotNull(message = "Trip ID is required")
    @Positive(message = "Trip ID must be greater than 0")
    private Long tripId;

    @NotNull(message = "Seats are required")
    @Positive(message = "Seats must be greater than 0")
    private Integer seats;

    public CreateBookingRequest() {
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }
}