package com.sahyadri.sahyadripooltrip.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sahyadri.sahyadripooltrip.dto.DriverBookingResponse;
import com.sahyadri.sahyadripooltrip.dto.DriverEmergencyContactRequest;
import com.sahyadri.sahyadripooltrip.dto.UpdateProfileRequest;
import com.sahyadri.sahyadripooltrip.dto.UserResponse;
import com.sahyadri.sahyadripooltrip.entity.Booking;
import com.sahyadri.sahyadripooltrip.entity.DriverProfile;
import com.sahyadri.sahyadripooltrip.entity.Trip;
import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.repository.BookingRepository;
import com.sahyadri.sahyadripooltrip.repository.DriverProfileRepository;
import com.sahyadri.sahyadripooltrip.repository.TripRepository;
import com.sahyadri.sahyadripooltrip.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

    private final UserRepository userRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final TripRepository tripRepository;
    private final BookingRepository bookingRepository;

    public DriverController(
            UserRepository userRepository,
            DriverProfileRepository driverProfileRepository,
            TripRepository tripRepository,
            BookingRepository bookingRepository) {

        this.userRepository = userRepository;
        this.driverProfileRepository = driverProfileRepository;
        this.tripRepository = tripRepository;
        this.bookingRepository = bookingRepository;
    }

    // =====================================================
    // GET DRIVER PROFILE
    // GET /api/driver/profile
    // =====================================================

    @GetMapping("/profile")
    public ResponseEntity<?> getDriverProfile(
            Authentication authentication) {

        String email = authentication.getName();

        User driver = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Driver not found"));

        return ResponseEntity.ok(
                new UserResponse(
                        driver.getUserId(),
                        driver.getName(),
                        driver.getEmail(),
                        driver.getPhone(),
                        driver.getRole(),
                        driver.getEmergencyContactName(),
                        driver.getEmergencyContactNumber(),
                        driver.getEmergencyContactRelation()));
    }

    // =====================================================
    // GET DRIVER EMERGENCY CONTACT
    // GET /api/driver/emergency-contact
    // =====================================================

    @GetMapping("/emergency-contact")
    public ResponseEntity<?> getDriverEmergencyContact(
            Authentication authentication) {

        String email = authentication.getName();

        User driver = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Driver not found"));

        DriverProfile profile = driverProfileRepository
                .findByUserId(driver.getUserId())
                .orElse(null);

        if (profile != null) {

            return ResponseEntity.ok(Map.of(
                    "emergencyContactName",
                    profile.getEmergencyContactName() == null
                            ? ""
                            : profile.getEmergencyContactName(),

                    "emergencyContactNumber",
                    profile.getEmergencyContactNumber() == null
                            ? ""
                            : profile.getEmergencyContactNumber(),

                    "emergencyContactRelation",
                    profile.getEmergencyContactRelation() == null
                            ? ""
                            : profile.getEmergencyContactRelation()
            ));
        }

        return ResponseEntity.ok(Map.of(
                "emergencyContactName",
                driver.getEmergencyContactName() == null
                        ? ""
                        : driver.getEmergencyContactName(),

                "emergencyContactNumber",
                driver.getEmergencyContactNumber() == null
                        ? ""
                        : driver.getEmergencyContactNumber(),

                "emergencyContactRelation",
                driver.getEmergencyContactRelation() == null
                        ? ""
                        : driver.getEmergencyContactRelation()
        ));
    }


    @GetMapping("/vehicle")
    public ResponseEntity<?> getDriverVehicle(Authentication authentication) {
        String email = authentication.getName();
        User driver = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));
        DriverProfile profile = driverProfileRepository.findByUserId(driver.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Driver vehicle profile not found"));
        return ResponseEntity.ok(Map.of(
                "vehicleType", profile.getVehicleType(),
                "seatingCapacity", profile.getSeatingCapacity(),
                "verificationStatus", profile.getVerificationStatus() == null ? "PENDING" : profile.getVerificationStatus().name()));
    }

    // =====================================================
    // UPDATE DRIVER EMERGENCY CONTACT
    // PUT /api/driver/emergency-contact
    // =====================================================

    @PutMapping("/emergency-contact")
    public ResponseEntity<?> updateEmergencyContact(
            @Valid @RequestBody DriverEmergencyContactRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User driver = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Driver not found"));

        DriverProfile profile = driverProfileRepository
                .findByUserId(driver.getUserId())
                .orElse(null);

        String emergencyName = request.getEmergencyContactName();

        if (emergencyName == null || emergencyName.isBlank()) {
            throw new IllegalArgumentException(
                    "Emergency contact name is required");
        }

        emergencyName = emergencyName.trim();

        String emergencyNumber = request.getEmergencyContactNumber();

        if (emergencyNumber == null || emergencyNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Emergency contact number is required");
        }

        emergencyNumber = emergencyNumber.trim();

        if (!emergencyNumber.matches("^[6-9]\\d{9}$")) {
            throw new IllegalArgumentException(
                    "Invalid emergency contact number");
        }

        String emergencyRelation =
                request.getEmergencyContactRelation();

        if (emergencyRelation == null ||
                emergencyRelation.isBlank()) {

            throw new IllegalArgumentException(
                    "Emergency contact relation is required");
        }

        emergencyRelation = emergencyRelation.trim();

        if (profile != null) {

            profile.setEmergencyContactName(emergencyName);
            profile.setEmergencyContactNumber(emergencyNumber);
            profile.setEmergencyContactRelation(emergencyRelation);

            driverProfileRepository.save(profile);

        } else {

            driver.setEmergencyContactName(emergencyName);
            driver.setEmergencyContactNumber(emergencyNumber);
            driver.setEmergencyContactRelation(emergencyRelation);

            userRepository.save(driver);
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Emergency contact updated successfully",
                "emergencyContactName", emergencyName,
                "emergencyContactNumber", emergencyNumber,
                "emergencyContactRelation", emergencyRelation
        ));
    }

    // =====================================================
    // UPDATE DRIVER PROFILE
    // PUT /api/driver/profile
    // =====================================================

    @PutMapping("/profile")
    public ResponseEntity<?> updateDriverProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User driver = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Driver not found"));

        if (request.getName() != null &&
                !request.getName().isBlank()) {

            driver.setName(request.getName().trim());
        }

        if (request.getPhone() != null &&
                !request.getPhone().isBlank()) {

            driver.setPhone(request.getPhone().trim());
        }

        User updatedDriver = userRepository.save(driver);

        return ResponseEntity.ok(
                new UserResponse(
                        updatedDriver.getUserId(),
                        updatedDriver.getName(),
                        updatedDriver.getEmail(),
                        updatedDriver.getPhone(),
                        updatedDriver.getRole(),
                        updatedDriver.getEmergencyContactName(),
                        updatedDriver.getEmergencyContactNumber(),
                        updatedDriver.getEmergencyContactRelation()));
    }

    // =====================================================
    // GET DRIVER BOOKINGS
    // GET /api/driver/bookings
    // =====================================================

    @GetMapping("/bookings")
    public ResponseEntity<?> getDriverBookings(
            Authentication authentication) {

        String email = authentication.getName();

        User driver = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Driver not found"));

        List<Trip> trips =
                tripRepository.findByDriverId(driver.getUserId());

        List<DriverBookingResponse> responses =
                new ArrayList<>();

        for (Trip trip : trips) {

            List<Booking> bookings =
                    bookingRepository.findByTripId(
                            trip.getTripId());

            for (Booking booking : bookings) {

                User traveler = userRepository
                        .findById(booking.getTravelerId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Traveler not found"));

                DriverBookingResponse response =
                        new DriverBookingResponse(
                                booking.getBookingId(),
                                trip.getTripId(),
                                traveler.getName(),
                                traveler.getEmail(),
                                trip.getFromLocation(),
                                trip.getToLocation(),
                                trip.getTravelDate(),
                                booking.getSeatsBooked(),
                                booking.getTotalAmount(),
                                booking.getStatus(),
                                booking.getBookedAt());

                responses.add(response);
            }
        }

        return ResponseEntity.ok(responses);
    }
}