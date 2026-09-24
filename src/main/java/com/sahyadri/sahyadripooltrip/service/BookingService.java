package com.sahyadri.sahyadripooltrip.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sahyadri.sahyadripooltrip.dto.CreateBookingRequest;
import com.sahyadri.sahyadripooltrip.entity.Booking;
import com.sahyadri.sahyadripooltrip.entity.Trip;
import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.repository.BookingRepository;
import com.sahyadri.sahyadripooltrip.repository.TripRepository;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final TripPricingService pricingService;

    public BookingService(
            BookingRepository bookingRepository,
            TripRepository tripRepository,
            TripPricingService pricingService) {

        this.bookingRepository = bookingRepository;
        this.tripRepository = tripRepository;
        this.pricingService = pricingService;
    }

    // =====================================================
    // CREATE BOOKING
    // Booking is created as PENDING_PAYMENT
    // =====================================================

    @Transactional
    public Booking createPendingBooking(
            CreateBookingRequest request,
            User traveler) {

        Trip trip = tripRepository.findByIdForUpdate(
                request.getTripId()).orElseThrow(() -> new IllegalArgumentException("Trip not found"));

        // =====================================================
        // CHECK TRIP STATUS
        // =====================================================

        if (!"ACTIVE".equalsIgnoreCase(trip.getStatus())) {
            throw new IllegalArgumentException(
                    "This trip is not active");
        }

        // =====================================================
        // PREVENT DUPLICATE CONFIRMED BOOKING
        // =====================================================

        boolean alreadyConfirmed = bookingRepository
                .existsByTripIdAndTravelerIdAndStatus(
                        trip.getTripId(),
                        traveler.getUserId(),
                        "CONFIRMED");

        if (alreadyConfirmed) {
            throw new IllegalArgumentException(
                    "You have already booked this trip");
        }

        // =====================================================
        // PREVENT DUPLICATE PENDING PAYMENT
        // =====================================================

        boolean alreadyPending = bookingRepository
                .existsByTripIdAndTravelerIdAndStatus(
                        trip.getTripId(),
                        traveler.getUserId(),
                        "PENDING_PAYMENT");

        if (alreadyPending) {
            throw new IllegalArgumentException(
                    "You already have a pending payment for this trip");
        }

        // =====================================================
        // WHOLE VEHICLE MODE
        // =====================================================

        if ("FULL_VEHICLE".equalsIgnoreCase(trip.getBookingMode())
                && request.getSeats() != trip.getTotalSeats()) {
            throw new IllegalArgumentException(
                    "This departure is sold as a whole vehicle. The complete vehicle must be booked.");
        }

        // =====================================================
        // CHECK AVAILABLE SEATS
        // =====================================================

        if (request.getSeats() > trip.getAvailableSeats()) {
            throw new IllegalArgumentException(
                    "Not enough seats available");
        }

        // =====================================================
        // CALCULATE TOTAL
        // =====================================================

        double totalAmount = request.getSeats() * trip.getPricePerSeat();

        // Service fee is charged once per booking, not once per passenger.
        if (trip.getServiceFee() != null) {
            totalAmount += trip.getServiceFee();
        }

        // =====================================================
        // RESERVE SEATS
        // =====================================================

        trip.setAvailableSeats(
                trip.getAvailableSeats()
                        - request.getSeats());

        // =====================================================
        // MARK TRIP FULL
        // =====================================================

        if (trip.getAvailableSeats() == 0) {
            trip.setStatus("FULL");
        }

        tripRepository.save(trip);

        // =====================================================
        // CREATE PENDING BOOKING
        // =====================================================

        Booking booking = new Booking();

        booking.setTripId(trip.getTripId());
        booking.setTravelerId(traveler.getUserId());
        booking.setSeatsBooked(request.getSeats());
        booking.setTotalAmount(totalAmount);
        booking.setStatus("PENDING_PAYMENT");

        return bookingRepository.save(booking);
    }

    // =====================================================
    // FIND BOOKING
    // =====================================================

    public Booking getBooking(Long bookingId) {

        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Booking not found"));
    }

    // =====================================================
    // CONFIRM BOOKING AFTER SUCCESSFUL PAYMENT
    // =====================================================

    @Transactional
    public Booking confirmBooking(Long bookingId) {

        Booking booking = getBooking(bookingId);

        if ("CONFIRMED".equalsIgnoreCase(
                booking.getStatus())) {

            return booking;
        }

        if (!"PENDING_PAYMENT".equalsIgnoreCase(
                booking.getStatus())) {

            throw new IllegalArgumentException(
                    "Booking cannot be confirmed from current status: "
                            + booking.getStatus());
        }

        booking.setStatus("CONFIRMED");

        return bookingRepository.save(booking);
    }

    // =====================================================
    // CANCEL PENDING BOOKING AND RELEASE SEATS
    // =====================================================

    @Transactional
    public Booking cancelPendingBooking(
            Long bookingId,
            String reason) {

        Booking booking = getBooking(bookingId);

        if (!"PENDING_PAYMENT".equalsIgnoreCase(
                booking.getStatus())) {

            throw new IllegalArgumentException(
                    "Only pending payment bookings can be cancelled");
        }

        Trip trip = tripRepository.findByIdForUpdate(
                booking.getTripId()).orElseThrow(
                        () -> new IllegalArgumentException(
                                "Trip not found"));

        // =====================================================
        // RELEASE RESERVED SEATS
        // =====================================================

        trip.setAvailableSeats(
                trip.getAvailableSeats()
                        + booking.getSeatsBooked());

        // =====================================================
        // RESTORE ACTIVE STATUS IF TRIP WAS FULL
        // =====================================================

        if ("FULL".equalsIgnoreCase(trip.getStatus())) {
            trip.setStatus("ACTIVE");
        }

        tripRepository.save(trip);

        // =====================================================
        // UPDATE BOOKING STATUS
        // =====================================================

        booking.setStatus("PAYMENT_FAILED");

        return bookingRepository.save(booking);
    }
}