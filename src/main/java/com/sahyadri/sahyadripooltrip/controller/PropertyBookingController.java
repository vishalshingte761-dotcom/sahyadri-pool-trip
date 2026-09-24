package com.sahyadri.sahyadripooltrip.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

import com.sahyadri.sahyadripooltrip.dto.CreatePropertyBookingRequest;
import com.sahyadri.sahyadripooltrip.dto.PropertyAvailabilityResponse;
import com.sahyadri.sahyadripooltrip.dto.PropertyBookingResponse;
import com.sahyadri.sahyadripooltrip.entity.Property;
import com.sahyadri.sahyadripooltrip.entity.PropertyBooking;
import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.repository.PropertyBookingRepository;
import com.sahyadri.sahyadripooltrip.repository.PropertyRepository;
import com.sahyadri.sahyadripooltrip.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/property-bookings")
public class PropertyBookingController {

        private final PropertyBookingRepository propertyBookingRepository;
        private final PropertyRepository propertyRepository;
        private final UserRepository userRepository;

        public PropertyBookingController(
                        PropertyBookingRepository propertyBookingRepository,
                        PropertyRepository propertyRepository,
                        UserRepository userRepository) {

                this.propertyBookingRepository = propertyBookingRepository;
                this.propertyRepository = propertyRepository;
                this.userRepository = userRepository;
        }

        // =====================================================
        // CREATE PROPERTY BOOKING
        // POST /api/property-bookings
        // =====================================================

        @PostMapping
        @Transactional
        public ResponseEntity<PropertyBookingResponse> createBooking(
                        @Valid @RequestBody CreatePropertyBookingRequest request,
                        Authentication authentication) {

                // -------------------------------------------------
                // GET LOGGED-IN TRAVELER
                // -------------------------------------------------

                String email = authentication.getName();

                User traveler = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Traveler not found"));

                // -------------------------------------------------
                // BASIC VALIDATION
                // -------------------------------------------------

                if (request.getPropertyId() == null) {
                        throw new IllegalArgumentException(
                                        "Property ID is required");
                }

                if (request.getRooms() == null || request.getRooms() <= 0) {
                        throw new IllegalArgumentException(
                                        "Rooms must be greater than 0");
                }

                // -------------------------------------------------
                // DATE VALIDATION
                // -------------------------------------------------

                LocalDate checkInDate = request.getCheckInDate();
                LocalDate checkOutDate = request.getCheckOutDate();

                if (checkInDate == null) {
                        throw new IllegalArgumentException(
                                        "Check-in date is required");
                }

                if (checkOutDate == null) {
                        throw new IllegalArgumentException(
                                        "Check-out date is required");
                }

                if (checkInDate.isBefore(LocalDate.now())) {
                        throw new IllegalArgumentException(
                                        "Check-in date cannot be in the past");
                }

                if (!checkOutDate.isAfter(checkInDate)) {
                        throw new IllegalArgumentException(
                                        "Check-out date must be after check-in date");
                }

                // -------------------------------------------------
                // FIND PROPERTY
                // -------------------------------------------------

                Property property = propertyRepository
                                .findByIdForUpdate(request.getPropertyId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Property not found"));

                if (!"ACTIVE".equalsIgnoreCase(property.getStatus())) {
                        throw new IllegalArgumentException(
                                        "This property is not active");
                }
                String requestedPayment = request.getPaymentMethod()==null?"PAY_AT_HOTEL":request.getPaymentMethod().trim().toUpperCase();
                if ("ONLINE".equalsIgnoreCase(property.getPaymentMode()) && !"ONLINE".equals(requestedPayment)) throw new IllegalArgumentException("This stay accepts online payment only");
                if ("PAY_AT_HOTEL".equalsIgnoreCase(property.getPaymentMode()) && !"PAY_AT_HOTEL".equals(requestedPayment)) throw new IllegalArgumentException("This stay accepts pay at hotel only");
                // -------------------------------------------------
                // CHECK DUPLICATE CONFIRMED BOOKING
                // -------------------------------------------------

                boolean duplicateBooking = propertyBookingRepository
                                .existsByPropertyIdAndTravelerIdAndCheckInDateAndCheckOutDateAndStatus(
                                                property.getPropertyId(),
                                                traveler.getUserId(),
                                                checkInDate,
                                                checkOutDate,
                                                "CONFIRMED");

                if (duplicateBooking) {
                        throw new IllegalArgumentException(
                                        "You already have a confirmed booking for this property and dates");
                }
                // -------------------------------------------------
                // FIND OVERLAPPING CONFIRMED BOOKINGS
                // -------------------------------------------------

                List<PropertyBooking> overlappingBookings = propertyBookingRepository
                                .findByPropertyIdAndStatusAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                                                property.getPropertyId(),
                                                "CONFIRMED",
                                                checkOutDate,
                                                checkInDate);

                // -------------------------------------------------
                // CALCULATE OCCUPIED ROOMS
                // -------------------------------------------------

                int occupiedRooms = overlappingBookings.stream()
                                .mapToInt(PropertyBooking::getRoomsBooked)
                                .sum();

                int availableRoomsForDates = property.getTotalRooms() - occupiedRooms;

                // -------------------------------------------------
                // CHECK DATE-WISE ROOM AVAILABILITY
                // -------------------------------------------------

                if (request.getRooms() > availableRoomsForDates) {
                        throw new IllegalArgumentException(
                                        "Not enough rooms available for selected dates");
                }

                // -------------------------------------------------
                // CALCULATE NUMBER OF NIGHTS
                // -------------------------------------------------

                long numberOfNights = ChronoUnit.DAYS.between(
                                checkInDate,
                                checkOutDate);

                // -------------------------------------------------
                // CALCULATE TOTAL AMOUNT
                // -------------------------------------------------

                double grossAmount = request.getRooms() * property.getPricePerNight() * numberOfNights;
                double discount = 0.0;
                String coupon = request.getCouponCode()==null?"":request.getCouponCode().trim();
                if(!coupon.isBlank() && property.getCouponCode()!=null && coupon.equalsIgnoreCase(property.getCouponCode()) && grossAmount >= (property.getCouponMinAmount()==null?0.0:property.getCouponMinAmount())) {
                    discount = Math.min(grossAmount, property.getCouponDiscount()==null?0.0:property.getCouponDiscount());
                }
                double totalAmount = grossAmount - discount;
                String paymentMethod = request.getPaymentMethod()==null?"PAY_AT_HOTEL":request.getPaymentMethod().trim().toUpperCase();
                if(!paymentMethod.equals("ONLINE") && !paymentMethod.equals("PAY_AT_HOTEL")) throw new IllegalArgumentException("Payment method must be ONLINE or PAY_AT_HOTEL");

                // -------------------------------------------------
                // CREATE BOOKING
                // -------------------------------------------------

                PropertyBooking booking = new PropertyBooking();

                booking.setPropertyId(property.getPropertyId());
                booking.setTravelerId(traveler.getUserId());
                booking.setRoomsBooked(request.getRooms());
                booking.setCheckInDate(checkInDate);
                booking.setCheckOutDate(checkOutDate);
                booking.setTotalAmount(totalAmount); booking.setDiscountAmount(discount); booking.setCouponCode(coupon.isBlank()?null:coupon); booking.setPaymentMethod(paymentMethod); booking.setPaymentStatus(paymentMethod.equals("ONLINE")?"ONLINE_PENDING":"PAY_AT_HOTEL");
                booking.setStatus("CONFIRMED");

                PropertyBooking savedBooking = propertyBookingRepository.save(booking);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(toResponse(savedBooking, property));
        }

        // =====================================================
        // GET MY PROPERTY BOOKINGS
        // GET /api/property-bookings/my
        // =====================================================

        @GetMapping("/my")
        public ResponseEntity<List<PropertyBookingResponse>> getMyBookings(
                        Authentication authentication) {

                String email = authentication.getName();

                User traveler = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Traveler not found"));

                List<PropertyBooking> bookings = propertyBookingRepository.findByTravelerId(
                                traveler.getUserId());

                List<PropertyBookingResponse> response = bookings.stream()
                                .map(booking -> {

                                        Property property = propertyRepository.findById(
                                                        booking.getPropertyId()).orElse(null);

                                        return toResponse(
                                                        booking,
                                                        property);
                                })
                                .toList();

                return ResponseEntity.ok(response);
        }

        // =====================================================
        // GET PROPERTY BOOKINGS
        // GET /api/property-bookings/property/{propertyId}
        // =====================================================

        @GetMapping("/property/{propertyId}")
        public ResponseEntity<List<PropertyBookingResponse>> getPropertyBookings(
                        @PathVariable Long propertyId,
                        Authentication authentication) {

                String email = authentication.getName();

                User owner = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Property owner not found"));

                Property property = propertyRepository.findById(propertyId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Property not found"));

                if (!property.getOwnerId()
                                .equals(owner.getUserId())) {

                        throw new IllegalArgumentException(
                                        "You are not the owner of this property");
                }

                List<PropertyBooking> bookings = propertyBookingRepository.findByPropertyId(
                                propertyId);

                List<PropertyBookingResponse> response = bookings.stream()
                                .map(booking -> toResponse(
                                                booking,
                                                property))
                                .toList();

                return ResponseEntity.ok(response);
        }
        // =====================================================
        // CHECK PROPERTY AVAILABILITY
        // GET /api/property-bookings/availability/{propertyId}
        // =====================================================

        @GetMapping("/availability/{propertyId}")
        public ResponseEntity<PropertyAvailabilityResponse> checkAvailability(
                        @PathVariable Long propertyId,
                        @org.springframework.web.bind.annotation.RequestParam LocalDate checkInDate,
                        @org.springframework.web.bind.annotation.RequestParam LocalDate checkOutDate) {

                // -------------------------------------------------
                // DATE VALIDATION
                // -------------------------------------------------

                if (checkInDate == null) {
                        throw new IllegalArgumentException(
                                        "Check-in date is required");
                }

                if (checkOutDate == null) {
                        throw new IllegalArgumentException(
                                        "Check-out date is required");
                }

                if (checkInDate.isBefore(LocalDate.now())) {
                        throw new IllegalArgumentException(
                                        "Check-in date cannot be in the past");
                }

                if (!checkOutDate.isAfter(checkInDate)) {
                        throw new IllegalArgumentException(
                                        "Check-out date must be after check-in date");
                }

                // -------------------------------------------------
                // FIND PROPERTY
                // -------------------------------------------------

                Property property = propertyRepository
                                .findById(propertyId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Property not found"));

                if (!"ACTIVE".equalsIgnoreCase(property.getStatus())) {
                        throw new IllegalArgumentException("This property is not active");
                }

                // -------------------------------------------------
                // FIND OVERLAPPING CONFIRMED BOOKINGS
                // -------------------------------------------------

                List<PropertyBooking> overlappingBookings = propertyBookingRepository
                                .findByPropertyIdAndStatusAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                                                propertyId,
                                                "CONFIRMED",
                                                checkOutDate,
                                                checkInDate);

                // -------------------------------------------------
                // CALCULATE BOOKED ROOMS
                // -------------------------------------------------

                int bookedRooms = overlappingBookings.stream()
                                .mapToInt(PropertyBooking::getRoomsBooked)
                                .sum();

                int availableRooms = property.getTotalRooms() - bookedRooms;

                // Safety
                if (availableRooms < 0) {
                        availableRooms = 0;
                }

                // -------------------------------------------------
                // RESPONSE
                // -------------------------------------------------

                PropertyAvailabilityResponse response = new PropertyAvailabilityResponse();

                response.setPropertyId(property.getPropertyId());

                response.setPropertyName(
                                property.getPropertyName());

                response.setCheckInDate(checkInDate);

                response.setCheckOutDate(checkOutDate);

                response.setTotalRooms(
                                property.getTotalRooms());

                response.setBookedRooms(bookedRooms);

                response.setAvailableRooms(availableRooms);

                return ResponseEntity.ok(response);
        }

        // =====================================================
        // CANCEL PROPERTY BOOKING
        // DELETE /api/property-bookings/{bookingId}
        // =====================================================

        @DeleteMapping("/{bookingId}")
        @Transactional
        public ResponseEntity<PropertyBookingResponse> cancelBooking(
                        @PathVariable Long bookingId,
                        Authentication authentication) {

                String email = authentication.getName();

                User traveler = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Traveler not found"));

                PropertyBooking booking = propertyBookingRepository.findById(bookingId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Booking not found"));

                if (!booking.getTravelerId()
                                .equals(traveler.getUserId())) {

                        throw new IllegalArgumentException(
                                        "You are not allowed to cancel this booking");
                }

                if (!"CONFIRMED".equalsIgnoreCase(
                                booking.getStatus())) {

                        throw new IllegalArgumentException(
                                        "Only confirmed bookings can be cancelled");
                }

                Property property = propertyRepository.findById(
                                booking.getPropertyId()).orElseThrow(
                                                () -> new IllegalArgumentException(
                                                                "Property not found"));

                // -------------------------------------------------
                // IMPORTANT:
                // Date-wise availability is calculated dynamically.
                //
                // So cancellation DOES NOT modify
                // property.availableRooms.
                // -------------------------------------------------

                booking.setStatus("CANCELLED");

                PropertyBooking cancelledBooking = propertyBookingRepository.save(booking);

                return ResponseEntity.ok(
                                toResponse(
                                                cancelledBooking,
                                                property));
        }


        @PostMapping("/{bookingId}/check-in")
        @Transactional
        public ResponseEntity<PropertyBookingResponse> checkIn(@PathVariable Long bookingId, Authentication authentication){
            PropertyBooking booking = ownedBooking(bookingId, authentication); Property property=propertyRepository.findById(booking.getPropertyId()).orElseThrow();
            if(!"CONFIRMED".equalsIgnoreCase(booking.getStatus())) throw new IllegalArgumentException("Only confirmed bookings can check in");
            booking.setCheckInStatus("CHECKED_IN"); booking.setCheckedInAt(java.time.LocalDateTime.now());
            return ResponseEntity.ok(toResponse(propertyBookingRepository.save(booking),property)); }
        @PostMapping("/{bookingId}/check-out")
        @Transactional
        public ResponseEntity<PropertyBookingResponse> checkOut(@PathVariable Long bookingId, Authentication authentication){
            PropertyBooking booking = ownedBooking(bookingId, authentication); Property property=propertyRepository.findById(booking.getPropertyId()).orElseThrow();
            if(!"CHECKED_IN".equalsIgnoreCase(booking.getCheckInStatus())) throw new IllegalArgumentException("Check-in must be completed first");
            booking.setCheckOutStatus("CHECKED_OUT"); booking.setCheckedOutAt(java.time.LocalDateTime.now()); booking.setStatus("COMPLETED");
            return ResponseEntity.ok(toResponse(propertyBookingRepository.save(booking),property)); }
        private PropertyBooking ownedBooking(Long bookingId, Authentication authentication){ String email=authentication.getName(); User owner=userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("Property owner not found")); PropertyBooking b=propertyBookingRepository.findById(bookingId).orElseThrow(()->new IllegalArgumentException("Booking not found")); Property p=propertyRepository.findById(b.getPropertyId()).orElseThrow(()->new IllegalArgumentException("Property not found")); if(!p.getOwnerId().equals(owner.getUserId())) throw new IllegalArgumentException("You are not the owner of this booking"); return b; }

        // =====================================================
        // CONVERT ENTITY TO RESPONSE DTO
        // =====================================================

        private PropertyBookingResponse toResponse(
                        PropertyBooking booking,
                        Property property) {

                PropertyBookingResponse response = new PropertyBookingResponse();

                response.setBookingId(
                                booking.getBookingId());

                response.setPropertyId(
                                booking.getPropertyId());

                if (property != null) {

                        response.setPropertyName(
                                        property.getPropertyName());

                        response.setPropertyType(
                                        property.getPropertyType());

                        response.setLocation(
                                        property.getLocation());

                        response.setPricePerNight(property.getPricePerNight()); response.setCheckInTime(property.getCheckInTime()); response.setCheckOutTime(property.getCheckOutTime());
                }

                response.setTravelerId(
                                booking.getTravelerId());

                response.setRoomsBooked(
                                booking.getRoomsBooked());

                response.setTotalAmount(
                                booking.getTotalAmount());

                response.setStatus(booking.getStatus()); response.setPaymentMethod(booking.getPaymentMethod()); response.setPaymentStatus(booking.getPaymentStatus()); response.setCouponCode(booking.getCouponCode()); response.setDiscountAmount(booking.getDiscountAmount()); response.setCheckInStatus(booking.getCheckInStatus()); response.setCheckOutStatus(booking.getCheckOutStatus());

                response.setBookedAt(
                                booking.getBookedAt());

                response.setCheckInDate(
                                booking.getCheckInDate());

                response.setCheckOutDate(
                                booking.getCheckOutDate());

                return response;
        }
}