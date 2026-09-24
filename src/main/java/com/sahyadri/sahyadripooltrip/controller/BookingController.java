package com.sahyadri.sahyadripooltrip.controller;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sahyadri.sahyadripooltrip.dto.CreateBookingRequest;
import com.sahyadri.sahyadripooltrip.dto.TravelerBookingResponse;
import com.sahyadri.sahyadripooltrip.entity.Booking;
import com.sahyadri.sahyadripooltrip.repository.AgencyRepository;
import com.sahyadri.sahyadripooltrip.entity.Trip;
import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.repository.BookingRepository;
import com.sahyadri.sahyadripooltrip.repository.TripRepository;
import com.sahyadri.sahyadripooltrip.repository.UserRepository;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

        private final BookingRepository bookingRepository;
        private final TripRepository tripRepository;
        private final UserRepository userRepository;
        private final AgencyRepository agencyRepository;

        public BookingController(
                        BookingRepository bookingRepository,
                        TripRepository tripRepository,
                        UserRepository userRepository,
                        AgencyRepository agencyRepository) {

                this.bookingRepository = bookingRepository;
                this.tripRepository = tripRepository;
                this.userRepository = userRepository;
                this.agencyRepository = agencyRepository;
        }

        // =====================================================
        // CREATE BOOKING
        // Traveler books seats
        // POST /api/bookings
        // =====================================================

        @Transactional
        @PostMapping
        public ResponseEntity<Booking> createBooking(
                        @Valid @RequestBody CreateBookingRequest request,
                        Authentication authentication) {

                String email = authentication.getName();

                User traveler = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Traveler not found"));


                Trip trip = tripRepository.findByIdForUpdate(
                                request.getTripId()).orElseThrow(
                                                () -> new IllegalArgumentException(
                                                                "Trip not found"));

                if (!"ACTIVE".equalsIgnoreCase(trip.getStatus())) {
                        throw new IllegalArgumentException(
                                        "This trip is not active");
                }

                // =====================================================
                // PREVENT DUPLICATE CONFIRMED BOOKING
                // =====================================================

                boolean alreadyBooked = bookingRepository
                                .existsByTripIdAndTravelerIdAndStatus(
                                                trip.getTripId(),
                                                traveler.getUserId(),
                                                "CONFIRMED");

                if (alreadyBooked) {
                        throw new IllegalArgumentException(
                                        "You have already booked this trip");
                }

                // =====================================================
                // WHOLE VEHICLE MODE
                // =====================================================

                if ("FULL_VEHICLE".equalsIgnoreCase(trip.getBookingMode())
                                && request.getSeats() != trip.getTotalSeats()) {
                        throw new IllegalArgumentException("This departure is sold as a whole vehicle. The complete vehicle must be booked.");
                }

                // =====================================================
                // CHECK AVAILABLE SEATS
                // =====================================================

                if (request.getSeats() > trip.getAvailableSeats()) {
                        throw new IllegalArgumentException(
                                        "Not enough seats available");
                }

                // =====================================================
                // CALCULATE TOTAL AMOUNT
                // =====================================================

                double totalAmount = request.getSeats() * trip.getPricePerSeat();

                // =====================================================
                // REDUCE AVAILABLE SEATS
                // =====================================================

                trip.setAvailableSeats(
                                trip.getAvailableSeats() - request.getSeats());

                // =====================================================
                // MARK TRIP FULL
                // =====================================================

                if (trip.getAvailableSeats() == 0) {
                        trip.setStatus("FULL");
                }

                tripRepository.save(trip);

                // =====================================================
                // CREATE BOOKING
                // =====================================================

                Booking booking = new Booking();

                booking.setTripId(trip.getTripId());
                booking.setTravelerId(traveler.getUserId());
                booking.setSeatsBooked(request.getSeats());
                booking.setTotalAmount(totalAmount);
                booking.setStatus("CONFIRMED");

                Booking savedBooking = bookingRepository.save(booking);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(savedBooking);
        }

        // =====================================================
        // GET MY BOOKINGS
        // Traveler can see his/her own bookings
        // GET /api/bookings/my
        // =====================================================

        @GetMapping("/my")
        public ResponseEntity<?> getMyBookings(
                        Authentication authentication) {

                String email = authentication.getName();

                User traveler = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Traveler not found"));

                List<Booking> bookings = bookingRepository.findByTravelerId(
                                traveler.getUserId());

                List<TravelerBookingResponse> responses = new ArrayList<>();

                for (Booking booking : bookings) {

                        Trip trip = tripRepository.findById(
                                        booking.getTripId()).orElseThrow(
                                                        () -> new IllegalArgumentException(
                                                                        "Trip not found"));

                        String providerName;
                        String providerEmail;
                        String providerPhone;
                        if (trip.getDriverId() != null) {
                                User driver = userRepository.findById(trip.getDriverId()).orElseThrow(
                                                () -> new IllegalArgumentException("Driver not found"));
                                providerName = driver.getName();
                                providerEmail = driver.getEmail();
                                providerPhone = driver.getPhone();
                        } else if (trip.getAgencyId() != null) {
                                var agency = agencyRepository.findById(trip.getAgencyId()).orElseThrow(
                                                () -> new IllegalArgumentException("Agency not found"));
                                providerName = agency.getBusinessName();
                                providerEmail = agency.getEmail();
                                providerPhone = agency.getMobile();
                        } else {
                                throw new IllegalArgumentException("Trip provider not found");
                        }

                        TravelerBookingResponse response = new TravelerBookingResponse(
                                        booking.getBookingId(),
                                        trip.getTripId(),
                                        providerName,
                                        providerEmail,
                                        providerPhone,
                                        trip.getFromLocation(),
                                        trip.getToLocation(),
                                        trip.getTravelDate(),
                                        trip.getDepartureTime(),
                                        booking.getSeatsBooked(),
                                        booking.getTotalAmount(),
                                        booking.getStatus(),
                                        booking.getBookedAt());

                        responses.add(response);
                }

                return ResponseEntity.ok(responses);
        }

        // =====================================================
        // GET BOOKINGS FOR A TRIP
        // Driver can see bookings of his trip
        // GET /api/bookings/trip/{tripId}
        // =====================================================

        @GetMapping("/trip/{tripId}")
        public ResponseEntity<?> getTripBookings(
                        @PathVariable Long tripId,
                        Authentication authentication) {

                String email = authentication.getName();

                User driver = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Driver not found"));

                Trip trip = tripRepository.findById(tripId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Trip not found"));

                if (trip.getDriverId() == null || !trip.getDriverId().equals(driver.getUserId())) {
                        throw new IllegalArgumentException(
                                        "You are not the driver of this trip");
                }

                return ResponseEntity.ok(
                                bookingRepository.findByTripId(tripId));
        }

        // =====================================================
        // CANCEL BOOKING
        // Traveler can cancel his/her own booking
        // DELETE /api/bookings/{bookingId}
        // =====================================================

        @Transactional
        @DeleteMapping("/{bookingId}")
        public ResponseEntity<?> cancelBooking(
                        @PathVariable Long bookingId,
                        Authentication authentication) {

                String email = authentication.getName();

                User traveler = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Traveler not found"));

                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Booking not found"));

                if (!booking.getTravelerId().equals(traveler.getUserId())) {
                        throw new IllegalArgumentException(
                                        "You are not allowed to cancel this booking");
                }

                if (!"CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
                        throw new IllegalArgumentException(
                                        "Only confirmed bookings can be cancelled");
                }

                Trip trip = tripRepository.findByIdForUpdate(
                                booking.getTripId()).orElseThrow(
                                                () -> new IllegalArgumentException(
                                                                "Trip not found"));

                // =====================================================
                // RETURN SEATS ONLY IF TRIP IS ACTIVE OR FULL
                // =====================================================

                if ("ACTIVE".equalsIgnoreCase(trip.getStatus())
                                || "FULL".equalsIgnoreCase(trip.getStatus())) {

                        trip.setAvailableSeats(
                                        trip.getAvailableSeats()
                                                        + booking.getSeatsBooked());

                        if ("FULL".equalsIgnoreCase(trip.getStatus())) {
                                trip.setStatus("ACTIVE");
                        }

                        tripRepository.save(trip);
                }

                // =====================================================
                // CANCEL BOOKING
                // =====================================================

                booking.setStatus("CANCELLED");

                Booking cancelledBooking = bookingRepository.save(booking);

                return ResponseEntity.ok(cancelledBooking);
        }

        // =====================================================
        // DRIVER CANCEL BOOKING
        // Driver can cancel booking from his own trip
        // DELETE /api/bookings/driver/{bookingId}
        // =====================================================

        @DeleteMapping("/driver/{bookingId}")
        public ResponseEntity<?> driverCancelBooking(
                        @PathVariable Long bookingId,
                        Authentication authentication) {

                String email = authentication.getName();

                User driver = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Driver not found"));

                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Booking not found"));

                Trip trip = tripRepository.findById(
                                booking.getTripId()).orElseThrow(
                                                () -> new IllegalArgumentException(
                                                                "Trip not found"));

                // =====================================================
                // VERIFY DRIVER OWNERSHIP
                // =====================================================

                if (trip.getDriverId() == null || !trip.getDriverId().equals(driver.getUserId())) {
                        throw new IllegalArgumentException(
                                        "You are not the driver of this trip");
                }

                if (!"CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
                        throw new IllegalArgumentException(
                                        "Only confirmed bookings can be cancelled");
                }

                // =====================================================
                // RETURN SEATS
                // =====================================================

                if ("ACTIVE".equalsIgnoreCase(trip.getStatus())
                                || "FULL".equalsIgnoreCase(trip.getStatus())) {

                        trip.setAvailableSeats(
                                        trip.getAvailableSeats()
                                                        + booking.getSeatsBooked());

                        if ("FULL".equalsIgnoreCase(trip.getStatus())) {
                                trip.setStatus("ACTIVE");
                        }

                        tripRepository.save(trip);
                }

                // =====================================================
                // CANCEL BOOKING
                // =====================================================

                booking.setStatus("CANCELLED");

                Booking cancelledBooking = bookingRepository.save(booking);

                return ResponseEntity.ok(cancelledBooking);
        }
}