package com.sahyadri.sahyadripooltrip.dto;

import java.math.BigDecimal;

public record PaymentOrderResponse(

                Long paymentId,

                Long bookingId,

                BigDecimal amount,

                String currency,

                String razorpayKeyId,

                String razorpayOrderId,

                String receipt

) {
}