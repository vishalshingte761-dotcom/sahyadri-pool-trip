package com.sahyadri.sahyadripooltrip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentVerifyRequest(

                @NotNull(message = "Payment ID is required") @Positive(message = "Payment ID must be greater than 0") Long paymentId,

                @NotBlank(message = "Razorpay order ID is required") String razorpayOrderId,

                @NotBlank(message = "Razorpay payment ID is required") String razorpayPaymentId,

                @NotBlank(message = "Razorpay signature is required") String razorpaySignature

) {
}