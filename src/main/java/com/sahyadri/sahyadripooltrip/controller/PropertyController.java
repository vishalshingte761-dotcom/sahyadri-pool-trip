package com.sahyadri.sahyadripooltrip.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

import com.sahyadri.sahyadripooltrip.dto.CreatePropertyRequest;
import com.sahyadri.sahyadripooltrip.dto.PropertySearchResponse;
import com.sahyadri.sahyadripooltrip.dto.UpdatePropertyRequest;
import com.sahyadri.sahyadripooltrip.entity.Property;
import com.sahyadri.sahyadripooltrip.entity.PropertyBooking;
import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.repository.PropertyBookingRepository;
import com.sahyadri.sahyadripooltrip.repository.PropertyRepository;
import com.sahyadri.sahyadripooltrip.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

        private final PropertyRepository propertyRepository;
        private final UserRepository userRepository;
        private final PropertyBookingRepository propertyBookingRepository;

        public PropertyController(
                        PropertyRepository propertyRepository,
                        UserRepository userRepository,
                        PropertyBookingRepository propertyBookingRepository) {

                this.propertyRepository = propertyRepository;
                this.userRepository = userRepository;
                this.propertyBookingRepository = propertyBookingRepository;
        }

        // =====================================================
        // GET ACTIVE PROPERTIES
        // GET /api/properties
        // =====================================================
        @GetMapping
        public ResponseEntity<?> getActiveProperties() {

                return ResponseEntity.ok(
                                propertyRepository.findByStatus("ACTIVE"));
        }

        // =====================================================
        // ADVANCED SEARCH PROPERTIES
        // GET /api/properties/search
        // =====================================================

        @GetMapping("/search")
        public ResponseEntity<?> searchProperties(
                        @RequestParam(required = false) String location,
                        @RequestParam(required = false) String propertyType,
                        @RequestParam(required = false) Double minPrice,
                        @RequestParam(required = false) Double maxPrice,
                        @RequestParam(required = false) Integer minRooms,
                        @RequestParam(required = false) LocalDate checkInDate,
                        @RequestParam(required = false) LocalDate checkOutDate) {

                // String validation
                if (location != null && location.isBlank()) {
                        throw new IllegalArgumentException("Location cannot be blank");
                }

                if (propertyType != null && propertyType.isBlank()) {
                        throw new IllegalArgumentException("Property type cannot be blank");
                }

                // Price validation
                if (minPrice != null && minPrice < 0) {
                        throw new IllegalArgumentException("Minimum price cannot be negative");
                }

                if (maxPrice != null && maxPrice < 0) {
                        throw new IllegalArgumentException("Maximum price cannot be negative");
                }

                if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
                        throw new IllegalArgumentException(
                                        "Minimum price cannot be greater than maximum price");
                }

                // Room validation
                if (minRooms != null && minRooms <= 0) {
                        throw new IllegalArgumentException(
                                        "Minimum rooms must be greater than 0");
                }

                // Date validation
                boolean datesProvided = checkInDate != null || checkOutDate != null;

                if (datesProvided) {

                        if (checkInDate == null || checkOutDate == null) {
                                throw new IllegalArgumentException(
                                                "Both check-in and check-out dates are required");
                        }

                        if (checkInDate.isBefore(LocalDate.now())) {
                                throw new IllegalArgumentException(
                                                "Check-in date cannot be in the past");
                        }

                        if (!checkOutDate.isAfter(checkInDate)) {
                                throw new IllegalArgumentException(
                                                "Check-out date must be after check-in date");
                        }
                }

                // Base search
                List<Property> properties = propertyRepository.advancedSearch(
                                location,
                                propertyType,
                                minPrice,
                                maxPrice);

                List<PropertySearchResponse> response = new ArrayList<>();

                for (Property property : properties) {

                        int availableRooms;

                        if (datesProvided) {

                                int requestedRooms = minRooms != null ? minRooms : 1;

                                List<PropertyBooking> overlappingBookings = propertyBookingRepository
                                                .findByPropertyIdAndStatusAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                                                                property.getPropertyId(),
                                                                "CONFIRMED",
                                                                checkOutDate,
                                                                checkInDate);

                                int bookedRooms = overlappingBookings.stream()
                                                .mapToInt(PropertyBooking::getRoomsBooked)
                                                .sum();

                                availableRooms = property.getTotalRooms() - bookedRooms;

                                if (availableRooms < requestedRooms) {
                                        continue;
                                }

                        } else {

                                availableRooms = property.getAvailableRooms();

                                if (minRooms != null &&
                                                availableRooms < minRooms) {
                                        continue;
                                }
                        }

                        PropertySearchResponse propertyResponse = new PropertySearchResponse();
                        propertyResponse.setPropertyId(property.getPropertyId()); propertyResponse.setOwnerId(property.getOwnerId()); propertyResponse.setPropertyName(property.getPropertyName()); propertyResponse.setPropertyType(property.getPropertyType()); propertyResponse.setLocation(property.getLocation()); propertyResponse.setDescription(property.getDescription()); propertyResponse.setTotalRooms(property.getTotalRooms()); propertyResponse.setAvailableRooms(availableRooms); propertyResponse.setPricePerNight(property.getPricePerNight()); propertyResponse.setStatus(property.getStatus()); propertyResponse.setCheckInDate(checkInDate); propertyResponse.setCheckOutDate(checkOutDate); propertyResponse.setPhotoUrl(property.getPhotoUrl()); propertyResponse.setCheckInTime(property.getCheckInTime()); propertyResponse.setCheckOutTime(property.getCheckOutTime()); propertyResponse.setPaymentMode(property.getPaymentMode()); propertyResponse.setCouponDiscount(property.getCouponDiscount()); propertyResponse.setCouponMinAmount(property.getCouponMinAmount());

                        response.add(propertyResponse);
                }

                return ResponseEntity.ok(response);
        }

        // =====================================================
        // GET MY PROPERTIES
        // GET /api/properties/my
        // =====================================================
        @GetMapping("/my")
        public ResponseEntity<?> getMyProperties(
                        Authentication authentication) {

                String email = authentication.getName();

                User owner = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Property owner not found"));

                return ResponseEntity.ok(
                                propertyRepository.findByOwnerId(
                                                owner.getUserId()));
        }

        // =====================================================
        // CREATE PROPERTY
        // POST /api/properties
        // =====================================================
        @PostMapping
        public ResponseEntity<Property> createProperty(
                        @Valid @RequestBody CreatePropertyRequest request,
                        Authentication authentication) {

                String email = authentication.getName();

                User owner = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Property owner not found"));

                Property property = new Property();

                property.setOwnerId(owner.getUserId());

                property.setPropertyName(
                                request.getPropertyName().trim());

                property.setPropertyType(
                                request.getPropertyType().trim().toUpperCase());

                property.setLocation(
                                request.getLocation().trim());

                property.setDescription(
                                request.getDescription());

                property.setTotalRooms(
                                request.getTotalRooms());

                property.setAvailableRooms(
                                request.getTotalRooms());

                property.setPricePerNight(
                                request.getPricePerNight());

                property.setStatus("ACTIVE");
                property.setPhotoUrl(request.getPhotoUrl());
                if(request.getCheckInTime()!=null && !request.getCheckInTime().isBlank()) property.setCheckInTime(request.getCheckInTime());
                if(request.getCheckOutTime()!=null && !request.getCheckOutTime().isBlank()) property.setCheckOutTime(request.getCheckOutTime());
                if(request.getPaymentMode()!=null && !request.getPaymentMode().isBlank()) property.setPaymentMode(request.getPaymentMode());
                property.setCouponCode(request.getCouponCode()); property.setCouponDiscount(request.getCouponDiscount()==null?0.0:request.getCouponDiscount()); property.setCouponMinAmount(request.getCouponMinAmount()==null?0.0:request.getCouponMinAmount());

                Property savedProperty = propertyRepository.save(property);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(savedProperty);
        }

        // =====================================================
        // UPDATE MY PROPERTY
        // PUT /api/properties/{propertyId}
        // =====================================================
        @PutMapping("/{propertyId}")
        public ResponseEntity<?> updateProperty(
                        @PathVariable Long propertyId,
                        @Valid @RequestBody UpdatePropertyRequest request,
                        Authentication authentication) {

                String email = authentication.getName();

                User owner = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Property owner not found"));

                Property property = propertyRepository
                                .findById(propertyId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Property not found"));

                if (!property.getOwnerId()
                                .equals(owner.getUserId())) {

                        throw new IllegalArgumentException(
                                        "You are not the owner of this property");
                }

                int occupiedRooms = property.getTotalRooms()
                                - property.getAvailableRooms();

                if (request.getTotalRooms() < occupiedRooms) {

                        throw new IllegalArgumentException(
                                        "Total rooms cannot be less than occupied rooms");
                }

                property.setPropertyName(
                                request.getPropertyName().trim());

                property.setPropertyType(
                                request.getPropertyType()
                                                .trim()
                                                .toUpperCase());

                property.setLocation(
                                request.getLocation().trim());

                property.setDescription(
                                request.getDescription());
                property.setPhotoUrl(request.getPhotoUrl());
                if(request.getCheckInTime()!=null && !request.getCheckInTime().isBlank()) property.setCheckInTime(request.getCheckInTime());
                if(request.getCheckOutTime()!=null && !request.getCheckOutTime().isBlank()) property.setCheckOutTime(request.getCheckOutTime());
                if(request.getPaymentMode()!=null && !request.getPaymentMode().isBlank()) property.setPaymentMode(request.getPaymentMode());
                property.setCouponCode(request.getCouponCode()); property.setCouponDiscount(request.getCouponDiscount()==null?0.0:request.getCouponDiscount()); property.setCouponMinAmount(request.getCouponMinAmount()==null?0.0:request.getCouponMinAmount());

                property.setPricePerNight(
                                request.getPricePerNight());

                property.setTotalRooms(
                                request.getTotalRooms());

                property.setAvailableRooms(
                                request.getTotalRooms()
                                                - occupiedRooms);

                Property updatedProperty = propertyRepository.save(property);

                return ResponseEntity.ok(updatedProperty);
        }

        // =====================================================
        // CANCEL PROPERTY
        // DELETE /api/properties/{propertyId}
        // =====================================================
        @DeleteMapping("/{propertyId}")
        public ResponseEntity<?> cancelProperty(
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

                if ("INACTIVE".equalsIgnoreCase(
                                property.getStatus())) {

                        throw new IllegalArgumentException(
                                        "Property is already inactive");
                }

                property.setStatus("INACTIVE");

                Property updatedProperty = propertyRepository.save(property);

                return ResponseEntity.ok(updatedProperty);
        }
}
