package com.sahyadri.sahyadripooltrip.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentOrderRequest(

                @NotNull(message = "Booking ID is required") @Positive(message = "Booking ID must be greater than 0") Long bookingId

) {
}