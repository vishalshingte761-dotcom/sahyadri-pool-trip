package com.sahyadri.sahyadripooltrip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sahyadri.sahyadripooltrip.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentWebhookController {

    private final PaymentService paymentService;

    public PaymentWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // =========================================================
    // RAZORPAY WEBHOOK
    // =========================================================

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestHeader(value = "X-Razorpay-Signature", required = false)
            String signature,
            @RequestHeader(value = "x-razorpay-event-id", required = false)
            String eventId,
            @RequestBody String payload) {

        paymentService.handleWebhook(
                payload,
                signature,
                eventId
        );

        return ResponseEntity.ok("Webhook received");
    }
}