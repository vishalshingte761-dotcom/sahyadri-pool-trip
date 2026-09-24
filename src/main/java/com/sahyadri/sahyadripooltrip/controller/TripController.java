package com.sahyadri.sahyadripooltrip.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;
import com.sahyadri.sahyadripooltrip.entity.Booking;
import com.sahyadri.sahyadripooltrip.repository.BookingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.sahyadri.sahyadripooltrip.dto.CreateTripRequest;
import com.sahyadri.sahyadripooltrip.entity.Trip;
import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.repository.TripRepository;
import com.sahyadri.sahyadripooltrip.repository.UserRepository;
import com.sahyadri.sahyadripooltrip.service.VerificationService;
import com.sahyadri.sahyadripooltrip.repository.DriverProfileRepository;
import com.sahyadri.sahyadripooltrip.entity.VerificationStatus;
import com.sahyadri.sahyadripooltrip.service.TripPricingService;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final VerificationService verificationService;
    private final TripPricingService pricingService;

    public TripController(
            TripRepository tripRepository,
            UserRepository userRepository,
            BookingRepository bookingRepository,
            DriverProfileRepository driverProfileRepository,
            VerificationService verificationService,
            TripPricingService pricingService) {

        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.driverProfileRepository = driverProfileRepository;
        this.verificationService = verificationService;
        this.pricingService = pricingService;
    }

    // =====================================================
    // GET ALL ACTIVE TRIPS
    // Traveler can see available trips
    // GET /api/trips
    // =====================================================
    @GetMapping
    public ResponseEntity<?> getAvailableTrips() {

        return ResponseEntity.ok(
                tripRepository.findByStatus("ACTIVE"));
    }

    // =====================================================
    // ADVANCED TRIP SEARCH
    // GET /api/trips/search
    //
    // Optional filters:
    // from
    // to
    // date
    // minPrice
    // maxPrice
    // minSeats
    // =====================================================
    @GetMapping("/search")
    public ResponseEntity<?> searchTrips(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minSeats,
            @RequestParam(required = false, defaultValue = "false") boolean returnTrip) {

        // -----------------------------
        // Clean text parameters
        // -----------------------------
        if (from != null && from.isBlank()) {
            from = null;
        } else if (from != null) {
            from = from.trim();
        }

        if (to != null && to.isBlank()) {
            to = null;
        } else if (to != null) {
            to = to.trim();
        }

        // -----------------------------
        // Parse travel date
        // -----------------------------
        LocalDate travelDate = null;

        if (date != null && !date.isBlank()) {

            try {

                travelDate = LocalDate.parse(date);

            } catch (Exception e) {

                throw new IllegalArgumentException(
                        "Invalid date format. Use YYYY-MM-DD");
            }

            if (travelDate.isBefore(LocalDate.now())) {

                throw new IllegalArgumentException(
                        "Travel date cannot be in the past");
            }
        }

        // -----------------------------
        // Validate price
        // -----------------------------
        if (minPrice != null && minPrice < 0) {

            throw new IllegalArgumentException(
                    "Minimum price cannot be negative");
        }

        if (maxPrice != null && maxPrice < 0) {

            throw new IllegalArgumentException(
                    "Maximum price cannot be negative");
        }

        if (minPrice != null
                && maxPrice != null
                && minPrice > maxPrice) {

            throw new IllegalArgumentException(
                    "Minimum price cannot be greater than maximum price");
        }

        // -----------------------------
        // Validate seats
        // -----------------------------
        if (minSeats != null && minSeats < 1) {

            throw new IllegalArgumentException(
                    "Minimum seats must be at least 1");
        }

        // -----------------------------
        // Advanced search
        // -----------------------------
        List<Trip> results = tripRepository.advancedSearch(
                from, to, travelDate, minPrice, maxPrice, minSeats);
        if (returnTrip) {
            results = results.stream().filter(t -> Boolean.TRUE.equals(t.getReturnTrip())).toList();
        }
        return ResponseEntity.ok(results);
    }

    @PreAuthorize("hasRole(\"ADMIN\")")
    @GetMapping("/pricing/rates")
    public ResponseEntity<?> rateCard() {
        return ResponseEntity.ok(Map.of(
                "platformMarginPercent", TripPricingService.PLATFORM_MARGIN_PERCENT,
                "serviceFee", TripPricingService.SERVICE_FEE,
                "vehicles", pricingService.rateCard()));
    }

    @GetMapping("/vehicles")
    public ResponseEntity<?> vehicleCatalog() {
        return ResponseEntity.ok(pricingService.vehicleCatalog());
    }

    @GetMapping("/pricing/quote")
    public ResponseEntity<?> quote(
            @RequestParam String vehicleType,
            @RequestParam int seats,
            @RequestParam double distanceKm,
            @RequestParam(defaultValue = "false") boolean returnTrip) {
        Map<String,Object> quote = pricingService.calculate(vehicleType, seats, distanceKm, returnTrip);
        quote.remove("platformMarginPercent");
        return ResponseEntity.ok(quote);
    }

    // =====================================================
    // CREATE TRIP
    // Driver can create a new trip
    // POST /api/trips
    // =====================================================
    @PostMapping
    public ResponseEntity<Trip> createTrip(
            @Valid @RequestBody CreateTripRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User driver = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));

        var driverProfile = driverProfileRepository.findByUserId(driver.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Driver profile is incomplete. Complete document verification before creating trips."));
        if (driverProfile.getVerificationStatus() != VerificationStatus.APPROVED) {
            throw new IllegalArgumentException("Driver verification is pending. Admin approval is required before creating trips.");
        }
        if (request.getTotalSeats() > driverProfile.getSeatingCapacity()) {
            throw new IllegalArgumentException("Published passenger seats cannot exceed the verified vehicle capacity of " + driverProfile.getSeatingCapacity());
        }

        Trip trip = new Trip();

        trip.setFromLocation(request.getFromLocation());
        trip.setToLocation(request.getToLocation());
        trip.setTravelDate(request.getTravelDate());
        trip.setDepartureTime(request.getDepartureTime());
        trip.setTotalSeats(request.getTotalSeats());
        trip.setAvailableSeats(request.getTotalSeats());
        trip.setVehicleType(driverProfile.getVehicleType());
        trip.setSeatingCapacity(driverProfile.getSeatingCapacity());
        boolean returnTrip = Boolean.TRUE.equals(request.getReturnTrip());
        trip.setReturnTrip(returnTrip);
        if (request.getDistanceKm() != null) {
            Map<String,Object> fare = pricingService.calculate(driverProfile.getVehicleType(), request.getTotalSeats(), request.getDistanceKm(), returnTrip);
            trip.setDistanceKm(request.getDistanceKm());
            trip.setVehicleRatePerKm(pricingService.rateFor(driverProfile.getVehicleType()));
            trip.setBaseTripFare((Double)fare.get("baseTripFare"));
            trip.setPlatformFee((Double)fare.get("platformFee"));
            trip.setServiceFee((Double)fare.get("serviceFee"));
            trip.setTotalTripFare((Double)fare.get("totalTripFare"));
            trip.setPricePerSeat((Double)fare.get("pricePerPerson"));
        } else {
            trip.setPricePerSeat(request.getPricePerSeat());
        }
        trip.setDriverId(driver.getUserId());
        trip.setStatus("ACTIVE");

        Trip savedTrip = tripRepository.save(trip);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedTrip);
    }
    // =====================================================
    // GET MY TRIPS
    // Driver can see only trips created by himself
    // GET /api/trips/my
    // =====================================================

    @GetMapping("/my")
    public ResponseEntity<?> getMyTrips(
            Authentication authentication) {

        String email = authentication.getName();

        User driver = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));

        return ResponseEntity.ok(
                tripRepository.findByDriverId(driver.getUserId()));
    }
    // =====================================================
    // UPDATE MY TRIP
    // Driver can update only his own trip
    // PUT /api/trips/{tripId}
    // =====================================================

    @PutMapping("/{tripId}")
    public ResponseEntity<?> updateTrip(
            @PathVariable Long tripId,
            @Valid @RequestBody CreateTripRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User driver = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));

        verificationService.requireApprovedDriver(driver.getUserId());

        Trip trip = tripRepository.findByIdForUpdate(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found"));

        if (!trip.getDriverId().equals(driver.getUserId())) {
            throw new IllegalArgumentException(
                    "You are not the driver of this trip");
        }

        if ("CANCELLED".equalsIgnoreCase(trip.getStatus())) {
            throw new IllegalArgumentException("Cancelled trips cannot be edited");
        }

        int oldTotalSeats = trip.getTotalSeats();
        int oldAvailableSeats = trip.getAvailableSeats();
        int bookedSeats = oldTotalSeats - oldAvailableSeats;

        if (request.getTotalSeats() < bookedSeats) {
            throw new IllegalArgumentException(
                    "Total seats cannot be less than already booked seats");
        }

        trip.setFromLocation(request.getFromLocation().trim());
        trip.setToLocation(request.getToLocation().trim());
        trip.setTravelDate(request.getTravelDate());
        trip.setDepartureTime(request.getDepartureTime());
        trip.setTotalSeats(request.getTotalSeats());
        boolean returnTrip = Boolean.TRUE.equals(request.getReturnTrip());
        trip.setReturnTrip(returnTrip);
        if (request.getDistanceKm() != null) {
            Map<String,Object> fare = pricingService.calculate(trip.getVehicleType(), request.getTotalSeats(), request.getDistanceKm(), returnTrip);
            trip.setDistanceKm(request.getDistanceKm());
            trip.setVehicleRatePerKm(pricingService.rateFor(trip.getVehicleType()));
            trip.setBaseTripFare((Double)fare.get("baseTripFare"));
            trip.setPlatformFee((Double)fare.get("platformFee"));
            trip.setServiceFee((Double)fare.get("serviceFee"));
            trip.setTotalTripFare((Double)fare.get("totalTripFare"));
            trip.setPricePerSeat((Double)fare.get("pricePerPerson"));
        } else {
            trip.setPricePerSeat(request.getPricePerSeat());
        }
        trip.setAvailableSeats(request.getTotalSeats() - bookedSeats);

        if (trip.getAvailableSeats() == 0) {
            trip.setStatus("FULL");
        } else {
            trip.setStatus("ACTIVE");
        }

        Trip updatedTrip = tripRepository.save(trip);

        return ResponseEntity.ok(updatedTrip);
    }
    // =====================================================
    // CANCEL TRIP
    // Driver can cancel only his own trip
    // Existing confirmed bookings will also be cancelled
    // DELETE /api/trips/{tripId}
    // =====================================================

    @DeleteMapping("/{tripId}")
    public ResponseEntity<?> cancelTrip(
            @PathVariable Long tripId,
            Authentication authentication) {

        String email = authentication.getName();

        User driver = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found"));

        // Make sure this trip belongs to logged-in driver
        if (!trip.getDriverId().equals(driver.getUserId())) {
            throw new IllegalArgumentException(
                    "You are not the driver of this trip");
        }

        // Prevent cancelling an already cancelled trip
        if ("CANCELLED".equalsIgnoreCase(trip.getStatus())) {
            throw new IllegalArgumentException(
                    "Trip is already cancelled");
        }

        // Find all confirmed bookings for this trip
        List<Booking> confirmedBookings = bookingRepository.findByTripIdAndStatus(
                tripId,
                "CONFIRMED");

        // Cancel all confirmed bookings
        for (Booking booking : confirmedBookings) {
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);
        }

        // Cancel the trip
        trip.setStatus("CANCELLED");

        Trip cancelledTrip = tripRepository.save(trip);

        return ResponseEntity.ok(cancelledTrip);
    }
}
