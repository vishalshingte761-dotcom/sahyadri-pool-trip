package com.sahyadri.sahyadripooltrip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import com.sahyadri.sahyadripooltrip.dto.AdminStatsResponse;
import com.sahyadri.sahyadripooltrip.repository.BookingRepository;
import com.sahyadri.sahyadripooltrip.repository.PropertyBookingRepository;
import com.sahyadri.sahyadripooltrip.repository.PropertyRepository;
import com.sahyadri.sahyadripooltrip.repository.TripRepository;
import com.sahyadri.sahyadripooltrip.repository.UserRepository;
import com.sahyadri.sahyadripooltrip.entity.Role;
import com.sahyadri.sahyadripooltrip.entity.VerificationStatus;
import com.sahyadri.sahyadripooltrip.repository.DriverProfileRepository;
import com.sahyadri.sahyadripooltrip.repository.AgencyRepository;
import com.sahyadri.sahyadripooltrip.repository.ComplaintRepository;
import com.sahyadri.sahyadripooltrip.repository.SosAlertRepository;
import com.sahyadri.sahyadripooltrip.service.VerificationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.sahyadri.sahyadripooltrip.dto.UserResponse;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

        private final UserRepository userRepository;
        private final TripRepository tripRepository;
        private final BookingRepository bookingRepository;
        private final PropertyRepository propertyRepository;
        private final PropertyBookingRepository propertyBookingRepository;
        private final DriverProfileRepository driverProfileRepository;
        private final AgencyRepository agencyRepository;
        private final ComplaintRepository complaintRepository;
        private final SosAlertRepository sosAlertRepository;
        private final VerificationService verificationService;

        public AdminController(
                        UserRepository userRepository,
                        TripRepository tripRepository,
                        BookingRepository bookingRepository,
                        PropertyRepository propertyRepository,
                        PropertyBookingRepository propertyBookingRepository,
                        DriverProfileRepository driverProfileRepository, AgencyRepository agencyRepository,
                        ComplaintRepository complaintRepository, SosAlertRepository sosAlertRepository,
                        VerificationService verificationService) {

                this.userRepository = userRepository;
                this.tripRepository = tripRepository;
                this.bookingRepository = bookingRepository;
                this.propertyRepository = propertyRepository;
                this.propertyBookingRepository = propertyBookingRepository;
                this.driverProfileRepository = driverProfileRepository;
                this.agencyRepository = agencyRepository;
                this.complaintRepository = complaintRepository;
                this.sosAlertRepository = sosAlertRepository;
                this.verificationService = verificationService;
        }

        // =====================================================
        // GET ALL USERS
        // GET /api/admin/users
        // =====================================================
        @GetMapping("/users")
        public ResponseEntity<?> getAllUsers() {

                List<UserResponse> users = userRepository.findAll()
                                .stream()
                                .map(user -> new UserResponse(
                                                user.getUserId(),
                                                user.getName(),
                                                user.getEmail(),
                                                user.getPhone(),
                                                user.getRole()))
                                .toList();

                return ResponseEntity.ok(users);
        }

        // =====================================================
        // GET ALL TRIPS
        // GET /api/admin/trips
        // =====================================================

        @GetMapping("/trips")
        public ResponseEntity<?> getAllTrips() {

                return ResponseEntity.ok(
                                tripRepository.findAll());
        }

        // =====================================================
        // GET ALL BOOKINGS
        // GET /api/admin/bookings
        // =====================================================

        @GetMapping("/bookings")
        public ResponseEntity<?> getAllBookings() {

                return ResponseEntity.ok(
                                bookingRepository.findAll());
        }

        // =====================================================
        // GET ALL PROPERTIES
        // GET /api/admin/properties
        // =====================================================

        @GetMapping("/properties")
        public ResponseEntity<?> getAllProperties() {

                return ResponseEntity.ok(
                                propertyRepository.findAll());
        }

        // =====================================================
        // GET ALL PROPERTY BOOKINGS
        // GET /api/admin/property-bookings
        // =====================================================

        @GetMapping("/property-bookings")
        public ResponseEntity<?> getAllPropertyBookings() {

                return ResponseEntity.ok(
                                propertyBookingRepository.findAll());
        }

        @GetMapping("/drivers/pending")
        public ResponseEntity<?> pendingDrivers() {
                return ResponseEntity.ok(driverProfileRepository.findByVerificationStatus(VerificationStatus.PENDING));
        }

        @GetMapping("/drivers")
        public ResponseEntity<?> allDrivers() {
                return ResponseEntity.ok(driverProfileRepository.findAll());
        }

        @PutMapping("/drivers/{id}/verification")
        public ResponseEntity<?> reviewDriver(
                        @PathVariable Long id,
                        @RequestBody Map<String, String> body,
                        Authentication auth) {

                String statusValue = body.get("status");

                if (statusValue == null || statusValue.isBlank()) {
                        throw new IllegalArgumentException(
                                        "Verification status is required");
                }

                VerificationStatus status;

                try {
                        status = VerificationStatus.valueOf(
                                        statusValue.trim().toUpperCase());
                } catch (IllegalArgumentException ex) {
                        throw new IllegalArgumentException(
                                        "Invalid verification status");
                }

                if (status != VerificationStatus.APPROVED
                                && status != VerificationStatus.REJECTED
                                && status != VerificationStatus.SUSPENDED
                                && status != VerificationStatus.UNDER_REVIEW) {

                        throw new IllegalArgumentException(
                                        "Invalid verification status");
                }

                Long adminId = userRepository
                                .findByEmail(auth.getName())
                                .orElseThrow(() -> new IllegalArgumentException("Admin not found"))
                                .getUserId();

                return ResponseEntity.ok(
                                verificationService.reviewDriver(
                                                id,
                                                status,
                                                body.get("rejectionReason"),
                                                adminId));
        }

        @GetMapping("/agencies/pending")
        public ResponseEntity<?> pendingAgencies() {
                return ResponseEntity.ok(agencyRepository.findByVerificationStatus(VerificationStatus.PENDING));
        }

        @GetMapping("/agencies")
        public ResponseEntity<?> allAgencies() {
                return ResponseEntity.ok(agencyRepository.findAll());
        }

        @PutMapping("/agencies/{id}/verification")
        public ResponseEntity<?> reviewAgency(
                        @PathVariable Long id,
                        @RequestBody Map<String, String> body,
                        Authentication auth) {

                String statusValue = body.get("status");

                if (statusValue == null || statusValue.isBlank()) {
                        throw new IllegalArgumentException(
                                        "Verification status is required");
                }

                VerificationStatus status;

                try {
                        status = VerificationStatus.valueOf(
                                        statusValue.trim().toUpperCase());
                } catch (IllegalArgumentException ex) {
                        throw new IllegalArgumentException(
                                        "Invalid verification status");
                }

                if (status != VerificationStatus.APPROVED
                                && status != VerificationStatus.REJECTED
                                && status != VerificationStatus.SUSPENDED
                                && status != VerificationStatus.UNDER_REVIEW) {

                        throw new IllegalArgumentException(
                                        "Invalid verification status");
                }

                Long adminId = userRepository
                                .findByEmail(auth.getName())
                                .orElseThrow(() -> new IllegalArgumentException("Admin not found"))
                                .getUserId();

                return ResponseEntity.ok(
                                verificationService.reviewAgency(
                                                id,
                                                status,
                                                body.get("rejectionReason"),
                                                adminId));
        }

        @GetMapping("/complaints")
        public ResponseEntity<?> complaints() {
                return ResponseEntity.ok(complaintRepository.findAllByOrderByCreatedAtDesc());
        }

        @GetMapping("/sos")
        public ResponseEntity<?> sos() {
                return ResponseEntity.ok(sosAlertRepository.findAll());
        }

        // =====================================================
        // ADMIN DASHBOARD STATISTICS
        // GET /api/admin/stats
        // =====================================================

        @GetMapping("/stats")
        public ResponseEntity<?> getStats() {

                // =================================================
                // USER STATISTICS
                // =================================================

                long totalUsers = userRepository.count();

                long totalTravelers = userRepository.countByRole(Role.TRAVELER);

                long totalDrivers = userRepository.countByRole(Role.DRIVER);

                long totalHotelOwners = userRepository.countByRole(Role.HOTEL_OWNER);

                // =================================================
                // TRIP STATISTICS
                // =================================================

                long totalTrips = tripRepository.count();

                long activeTrips = tripRepository.countByStatus("ACTIVE");

                long cancelledTrips = tripRepository.countByStatus("CANCELLED");

                // =================================================
                // TRIP BOOKING STATISTICS
                // =================================================

                long totalBookings = bookingRepository.count();

                long confirmedBookings = bookingRepository.countByStatus("CONFIRMED");

                long cancelledBookings = bookingRepository.countByStatus("CANCELLED");

                // =================================================
                // PROPERTY STATISTICS
                // =================================================

                long totalProperties = propertyRepository.count();

                long activeProperties = propertyRepository.countByStatus("ACTIVE");

                long inactiveProperties = propertyRepository.countByStatus("INACTIVE");

                // =================================================
                // PROPERTY BOOKING STATISTICS
                // =================================================

                long totalPropertyBookings = propertyBookingRepository.count();

                long confirmedPropertyBookings = propertyBookingRepository.countByStatus("CONFIRMED");

                long cancelledPropertyBookings = propertyBookingRepository.countByStatus("CANCELLED");

                // =================================================
                // CREATE RESPONSE
                // =================================================

                AdminStatsResponse response = new AdminStatsResponse(
                                totalUsers,
                                totalTravelers,
                                totalDrivers,
                                totalHotelOwners,

                                totalTrips,
                                activeTrips,
                                cancelledTrips,

                                totalBookings,
                                confirmedBookings,
                                cancelledBookings,

                                totalProperties,
                                activeProperties,
                                inactiveProperties,

                                totalPropertyBookings,
                                confirmedPropertyBookings,
                                cancelledPropertyBookings);

                return ResponseEntity.ok(response);
        }
}