package com.sahyadri.sahyadripooltrip.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.sahyadri.sahyadripooltrip.dto.PaymentOrderRequest;
import com.sahyadri.sahyadripooltrip.dto.PaymentOrderResponse;
import com.sahyadri.sahyadripooltrip.dto.PaymentResponse;
import com.sahyadri.sahyadripooltrip.dto.PaymentVerifyRequest;
import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.repository.UserRepository;
import com.sahyadri.sahyadripooltrip.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;

    public PaymentController(
            PaymentService paymentService,
            UserRepository userRepository) {

        this.paymentService = paymentService;
        this.userRepository = userRepository;
    }

    // =========================================================
    // CREATE RAZORPAY ORDER
    // =========================================================

    @PostMapping("/order")
    public ResponseEntity<PaymentOrderResponse> createOrder(
            @Valid @RequestBody PaymentOrderRequest request,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        PaymentOrderResponse response = paymentService.createPaymentOrder(
                request.bookingId(),
                user.getUserId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================================================
    // VERIFY PAYMENT
    // =========================================================

    @PostMapping("/verify")
    public ResponseEntity<PaymentResponse> verifyPayment(
            @Valid @RequestBody PaymentVerifyRequest request,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        PaymentResponse response = paymentService.verifyPayment(
                request,
                user.getUserId());

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // GET PAYMENT
    // =========================================================

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable Long paymentId,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        PaymentResponse response = paymentService.getPayment(
                paymentId,
                user.getUserId());

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // AUTHENTICATED USER
    // =========================================================

    private User getAuthenticatedUser(Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("Authentication required");
        }

        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));
    }
}