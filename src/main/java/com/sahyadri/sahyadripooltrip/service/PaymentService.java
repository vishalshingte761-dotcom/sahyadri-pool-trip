package com.sahyadri.sahyadripooltrip.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

import com.sahyadri.sahyadripooltrip.dto.PaymentOrderResponse;
import com.sahyadri.sahyadripooltrip.dto.PaymentResponse;
import com.sahyadri.sahyadripooltrip.dto.PaymentVerifyRequest;
import com.sahyadri.sahyadripooltrip.entity.Booking;
import com.sahyadri.sahyadripooltrip.entity.Payment;
import com.sahyadri.sahyadripooltrip.payment.PaymentStatus;
import com.sahyadri.sahyadripooltrip.repository.PaymentRepository;

@Service
public class PaymentService {

        private final PaymentRepository paymentRepository;
        private final BookingService bookingService;

        private final String keyId;
        private final String keySecret;
        private final String webhookSecret;

        public PaymentService(
                        PaymentRepository paymentRepository,
                        BookingService bookingService,
                        @Value("${razorpay.key-id}") String keyId,
                        @Value("${razorpay.key-secret}") String keySecret,
                        @Value("${razorpay.webhook-secret}") String webhookSecret) {

                this.paymentRepository = paymentRepository;
                this.bookingService = bookingService;
                this.keyId = keyId;
                this.keySecret = keySecret;
                this.webhookSecret = webhookSecret;
        }

        // =========================================================
        // CREATE RAZORPAY ORDER
        // =========================================================

        @Transactional
        public PaymentOrderResponse createPaymentOrder(
                        Long bookingId,
                        Long authenticatedUserId) {

                Booking booking = bookingService.getBooking(bookingId);

                // ---------------------------------------------------------
                // VERIFY BOOKING OWNER
                // ---------------------------------------------------------

                if (!booking.getTravelerId().equals(authenticatedUserId)) {
                        throw new IllegalArgumentException(
                                        "You are not allowed to pay for this booking");
                }

                // ---------------------------------------------------------
                // CHECK BOOKING STATUS
                // ---------------------------------------------------------

                if (!"PENDING_PAYMENT".equalsIgnoreCase(booking.getStatus())) {
                        throw new IllegalArgumentException(
                                        "Payment can only be created for a pending payment booking");
                }

                // ---------------------------------------------------------
                // RETURN EXISTING CREATED PAYMENT
                // ---------------------------------------------------------

                var existingPayment = paymentRepository.findByBookingIdAndStatus(
                                bookingId,
                                PaymentStatus.CREATED);

                if (existingPayment.isPresent()) {

                        Payment existing = existingPayment.get();

                        return new PaymentOrderResponse(
                                        existing.getId(),
                                        existing.getBookingId(),
                                        existing.getAmount(),
                                        existing.getCurrency(),
                                        keyId,
                                        existing.getRazorpayOrderId(),
                                        existing.getReceipt());
                }

                // ---------------------------------------------------------
                // SERVER-SIDE AMOUNT
                // ---------------------------------------------------------

                BigDecimal amount = BigDecimal.valueOf(booking.getTotalAmount());

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException(
                                        "Invalid booking amount");
                }

                long amountInPaise = amount.multiply(BigDecimal.valueOf(100))
                                .longValueExact();

                // ---------------------------------------------------------
                // UNIQUE RECEIPT
                // ---------------------------------------------------------

                String receipt = "SPT_"
                                + bookingId
                                + "_"
                                + UUID.randomUUID()
                                                .toString()
                                                .replace("-", "")
                                                .substring(0, 12);

                try {

                        RazorpayClient razorpayClient = new RazorpayClient(
                                        keyId,
                                        keySecret);

                        JSONObject options = new JSONObject();

                        options.put(
                                        "amount",
                                        amountInPaise);

                        options.put(
                                        "currency",
                                        "INR");

                        options.put(
                                        "receipt",
                                        receipt);

                        options.put(
                                        "notes",
                                        new JSONObject()
                                                        .put(
                                                                        "bookingId",
                                                                        bookingId.toString())
                                                        .put(
                                                                        "userId",
                                                                        authenticatedUserId.toString())
                                                        .toString());

                        Order order = razorpayClient.orders.create(options);

                        String razorpayOrderId = order.get("id");

                        // -----------------------------------------------------
                        // SAVE PAYMENT
                        // -----------------------------------------------------

                        Payment payment = new Payment();

                        payment.setBookingId(
                                        bookingId);

                        payment.setUserId(
                                        authenticatedUserId);

                        payment.setAmount(
                                        amount);

                        payment.setCurrency(
                                        "INR");

                        payment.setStatus(
                                        PaymentStatus.CREATED);

                        payment.setRazorpayOrderId(
                                        razorpayOrderId);

                        payment.setReceipt(
                                        receipt);

                        Payment savedPayment = paymentRepository.save(payment);

                        return new PaymentOrderResponse(
                                        savedPayment.getId(),
                                        bookingId,
                                        amount,
                                        "INR",
                                        keyId,
                                        razorpayOrderId,
                                        receipt);

                } catch (Exception e) {

                        throw new IllegalStateException(
                                        "Unable to create payment order",
                                        e);
                }
        }

        // =========================================================
        // VERIFY PAYMENT FROM FRONTEND
        // =========================================================

        @Transactional
        public PaymentResponse verifyPayment(
                        PaymentVerifyRequest request,
                        Long authenticatedUserId) {

                Payment payment = paymentRepository.findById(
                                request.paymentId())
                                .orElseThrow(
                                                () -> new IllegalArgumentException(
                                                                "Payment not found"));

                // ---------------------------------------------------------
                // VERIFY OWNER
                // ---------------------------------------------------------

                if (!payment.getUserId().equals(
                                authenticatedUserId)) {

                        throw new IllegalArgumentException(
                                        "You are not allowed to verify this payment");
                }

                // ---------------------------------------------------------
                // IDEMPOTENCY
                // ---------------------------------------------------------

                if (payment.getStatus() == PaymentStatus.SUCCESS) {
                        return toResponse(payment);
                }

                // ---------------------------------------------------------
                // VERIFY ORDER ID
                // ---------------------------------------------------------

                if (!payment.getRazorpayOrderId().equals(
                                request.razorpayOrderId())) {

                        throw new IllegalArgumentException(
                                        "Razorpay order ID does not match");
                }

                // ---------------------------------------------------------
                // VERIFY SIGNATURE
                // ---------------------------------------------------------

                try {

                        JSONObject attributes = new JSONObject();

                        attributes.put(
                                        "razorpay_order_id",
                                        request.razorpayOrderId());

                        attributes.put(
                                        "razorpay_payment_id",
                                        request.razorpayPaymentId());

                        attributes.put(
                                        "razorpay_signature",
                                        request.razorpaySignature());

                        boolean verified = Utils.verifyPaymentSignature(
                                        attributes,
                                        keySecret);

                        if (!verified) {

                                payment.setStatus(
                                                PaymentStatus.FAILED);

                                payment.setFailureReason(
                                                "Payment signature verification failed");

                                paymentRepository.save(payment);

                                throw new IllegalArgumentException(
                                                "Payment verification failed");
                        }

                        // -----------------------------------------------------
                        // SUCCESS
                        // -----------------------------------------------------

                        payment.setRazorpayPaymentId(
                                        request.razorpayPaymentId());

                        payment.setRazorpaySignature(
                                        request.razorpaySignature());

                        payment.setStatus(
                                        PaymentStatus.SUCCESS);

                        payment.setPaidAt(
                                        LocalDateTime.now());

                        Payment savedPayment = paymentRepository.save(payment);

                        // -----------------------------------------------------
                        // CONFIRM BOOKING
                        // -----------------------------------------------------

                        bookingService.confirmBooking(
                                        payment.getBookingId());

                        return toResponse(savedPayment);

                } catch (IllegalArgumentException e) {

                        throw e;

                } catch (Exception e) {

                        throw new IllegalStateException(
                                        "Payment verification failed",
                                        e);
                }
        }

        // =========================================================
        // RAZORPAY WEBHOOK
        // =========================================================

        @Transactional
        public void handleWebhook(
                        String payload,
                        String signature,
                        String eventId) {

                // ---------------------------------------------------------
                // BASIC VALIDATION
                // ---------------------------------------------------------

                if (signature == null || signature.isBlank()) {
                        throw new IllegalArgumentException(
                                        "Missing Razorpay webhook signature");
                }

                if (payload == null || payload.isBlank()) {
                        throw new IllegalArgumentException(
                                        "Empty webhook payload");
                }

                if (webhookSecret == null || webhookSecret.isBlank()) {
                        throw new IllegalStateException(
                                        "Razorpay webhook secret is not configured");
                }

                if (eventId == null || eventId.isBlank()) {
                        throw new IllegalArgumentException(
                                        "Missing Razorpay event ID");
                }

                // ---------------------------------------------------------
                // VERIFY WEBHOOK SIGNATURE
                // ---------------------------------------------------------

                try {

                        boolean valid = Utils.verifyWebhookSignature(
                                        payload,
                                        signature,
                                        webhookSecret);

                        if (!valid) {
                                throw new IllegalArgumentException(
                                                "Invalid Razorpay webhook signature");
                        }

                } catch (com.razorpay.RazorpayException e) {

                        throw new IllegalStateException(
                                        "Unable to verify Razorpay webhook signature",
                                        e);
                }

                // ---------------------------------------------------------
                // PARSE EVENT
                // ---------------------------------------------------------

                JSONObject webhook = new JSONObject(payload);

                String event = webhook.optString("event");

                if (event == null || event.isBlank()) {
                        return;
                }

                // ---------------------------------------------------------
                // HANDLE EVENT
                // ---------------------------------------------------------

                switch (event) {

                        case "payment.captured":
                                handlePaymentCaptured(webhook);
                                break;

                        case "payment.failed":
                                handlePaymentFailed(webhook);
                                break;

                        case "order.paid":
                                handleOrderPaid(webhook);
                                break;

                        default:
                                // Ignore events that our system does not need.
                                break;
                }
        }

        // =========================================================
        // PAYMENT CAPTURED
        // =========================================================

        private void handlePaymentCaptured(
                        JSONObject webhook) {

                JSONObject payload = webhook.optJSONObject("payload");

                if (payload == null) {
                        return;
                }

                JSONObject paymentEntity = getEntity(
                                payload,
                                "payment");

                if (paymentEntity == null) {
                        return;
                }

                String razorpayPaymentId = paymentEntity.optString("id");

                String razorpayOrderId = paymentEntity.optString("order_id");

                if (razorpayPaymentId.isBlank()
                                || razorpayOrderId.isBlank()) {
                        return;
                }

                Payment payment = paymentRepository
                                .findByRazorpayOrderId(
                                                razorpayOrderId)
                                .orElse(null);

                if (payment == null) {
                        return;
                }

                // ---------------------------------------------------------
                // ALREADY SUCCESS
                // ---------------------------------------------------------

                if (payment.getStatus() == PaymentStatus.SUCCESS) {

                        return;
                }

                // ---------------------------------------------------------
                // SAVE RAZORPAY PAYMENT DETAILS
                // ---------------------------------------------------------

                payment.setRazorpayPaymentId(
                                razorpayPaymentId);

                payment.setStatus(
                                PaymentStatus.SUCCESS);

                payment.setPaidAt(
                                LocalDateTime.now());

                paymentRepository.save(payment);

                // ---------------------------------------------------------
                // CONFIRM BOOKING
                // ---------------------------------------------------------

                Booking booking = bookingService.getBooking(
                                payment.getBookingId());

                if ("PENDING_PAYMENT".equalsIgnoreCase(
                                booking.getStatus())) {

                        bookingService.confirmBooking(
                                        payment.getBookingId());
                }
        }

        // =========================================================
        // PAYMENT FAILED
        // =========================================================

        private void handlePaymentFailed(
                        JSONObject webhook) {

                JSONObject payload = webhook.optJSONObject("payload");

                if (payload == null) {
                        return;
                }

                JSONObject paymentEntity = getEntity(
                                payload,
                                "payment");

                if (paymentEntity == null) {
                        return;
                }

                String razorpayPaymentId = paymentEntity.optString("id");

                String razorpayOrderId = paymentEntity.optString("order_id");

                String errorDescription = paymentEntity.optString(
                                "error_description");

                if (razorpayOrderId.isBlank()) {
                        return;
                }

                Payment payment = paymentRepository
                                .findByRazorpayOrderId(
                                                razorpayOrderId)
                                .orElse(null);

                if (payment == null) {
                        return;
                }

                // ---------------------------------------------------------
                // DO NOT CHANGE SUCCESS PAYMENT
                // ---------------------------------------------------------

                if (payment.getStatus() == PaymentStatus.SUCCESS) {

                        return;
                }

                if (!razorpayPaymentId.isBlank()) {

                        payment.setRazorpayPaymentId(
                                        razorpayPaymentId);
                }

                payment.setStatus(
                                PaymentStatus.FAILED);

                payment.setFailureReason(
                                errorDescription.isBlank()
                                                ? "Razorpay payment failed"
                                                : errorDescription);

                paymentRepository.save(payment);

                // ---------------------------------------------------------
                // RELEASE BOOKING SEATS
                // ---------------------------------------------------------

                Booking booking = bookingService.getBooking(
                                payment.getBookingId());

                if ("PENDING_PAYMENT".equalsIgnoreCase(
                                booking.getStatus())) {

                        bookingService.cancelPendingBooking(
                                        payment.getBookingId(),
                                        payment.getFailureReason());
                }
        }

        // =========================================================
        // ORDER PAID
        // =========================================================

        private void handleOrderPaid(
                        JSONObject webhook) {

                JSONObject payload = webhook.optJSONObject("payload");

                if (payload == null) {
                        return;
                }

                JSONObject orderEntity = getEntity(
                                payload,
                                "order");

                if (orderEntity == null) {
                        return;
                }

                String razorpayOrderId = orderEntity.optString("id");

                if (razorpayOrderId.isBlank()) {
                        return;
                }

                Payment payment = paymentRepository
                                .findByRazorpayOrderId(
                                                razorpayOrderId)
                                .orElse(null);

                if (payment == null) {
                        return;
                }

                // ---------------------------------------------------------
                // ALREADY SUCCESS
                // ---------------------------------------------------------

                if (payment.getStatus() == PaymentStatus.SUCCESS) {

                        return;
                }

                /*
                 * payment.captured normally handles the actual
                 * payment confirmation.
                 *
                 * order.paid is therefore treated as a
                 * reconciliation/fallback event.
                 */

                payment.setStatus(
                                PaymentStatus.SUCCESS);

                payment.setPaidAt(
                                LocalDateTime.now());

                paymentRepository.save(payment);

                Booking booking = bookingService.getBooking(
                                payment.getBookingId());

                if ("PENDING_PAYMENT".equalsIgnoreCase(
                                booking.getStatus())) {

                        bookingService.confirmBooking(
                                        payment.getBookingId());
                }
        }

        // =========================================================
        // WEBHOOK ENTITY HELPER
        // =========================================================

        private JSONObject getEntity(
                        JSONObject payload,
                        String entityName) {

                JSONObject wrapper = payload.optJSONObject(entityName);

                if (wrapper == null) {
                        return null;
                }

                return wrapper.optJSONObject("entity");
        }

        // =========================================================
        // GET PAYMENT
        // =========================================================

        public PaymentResponse getPayment(
                        Long paymentId,
                        Long authenticatedUserId) {

                Payment payment = paymentRepository.findById(
                                paymentId)
                                .orElseThrow(
                                                () -> new IllegalArgumentException(
                                                                "Payment not found"));

                if (!payment.getUserId().equals(
                                authenticatedUserId)) {

                        throw new IllegalArgumentException(
                                        "You are not allowed to view this payment");
                }

                return toResponse(payment);
        }

        // =========================================================
        // ENTITY → RESPONSE
        // =========================================================

        private PaymentResponse toResponse(
                        Payment payment) {

                return new PaymentResponse(
                                payment.getId(),
                                payment.getBookingId(),
                                payment.getAmount(),
                                payment.getCurrency(),
                                payment.getStatus(),
                                payment.getPaymentMethod(),
                                payment.getRazorpayOrderId(),
                                payment.getRazorpayPaymentId(),
                                payment.getFailureReason());
        }
}