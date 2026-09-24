package com.sahyadri.sahyadripooltrip.dto;

import java.math.BigDecimal;

import com.sahyadri.sahyadripooltrip.payment.PaymentMethod;
import com.sahyadri.sahyadripooltrip.payment.PaymentStatus;

public record PaymentResponse(

                Long paymentId,

                Long bookingId,

                BigDecimal amount,

                String currency,

                PaymentStatus status,

                PaymentMethod paymentMethod,

                String razorpayOrderId,

                String razorpayPaymentId,

                String failureReason

) {
}