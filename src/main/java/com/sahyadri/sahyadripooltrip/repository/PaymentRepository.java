package com.sahyadri.sahyadripooltrip.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sahyadri.sahyadripooltrip.entity.Payment;
import com.sahyadri.sahyadripooltrip.payment.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    Optional<Payment> findByBookingIdAndStatus(
            Long bookingId,
            PaymentStatus status);

    boolean existsByRazorpayPaymentId(String razorpayPaymentId);
}