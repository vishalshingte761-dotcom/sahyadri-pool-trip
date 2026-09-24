package com.sahyadri.sahyadripooltrip.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sahyadri.sahyadripooltrip.entity.PropertyBooking;

public interface PropertyBookingRepository
                extends JpaRepository<PropertyBooking, Long> {

        boolean existsByPropertyIdAndTravelerIdAndStatus(
                        Long propertyId,
                        Long travelerId,
                        String status);

        boolean existsByPropertyIdAndTravelerIdAndCheckInDateAndCheckOutDateAndStatus(
                        Long propertyId,
                        Long travelerId,
                        LocalDate checkInDate,
                        LocalDate checkOutDate,
                        String status);

        // =====================================================
        // GET BOOKINGS BY TRAVELER
        // =====================================================

        List<PropertyBooking> findByTravelerId(Long travelerId);

        // =====================================================
        // GET BOOKINGS FOR A PROPERTY
        // =====================================================

        List<PropertyBooking> findByPropertyId(Long propertyId);

        // =====================================================
        // GET BOOKINGS FOR A PROPERTY BY STATUS
        // =====================================================

        List<PropertyBooking> findByPropertyIdAndStatus(
                        Long propertyId,
                        String status);

        // =====================================================
        // GET OVERLAPPING CONFIRMED BOOKINGS
        //
        // Existing booking overlaps requested dates when:
        //
        // existing check-in < requested check-out
        // AND
        // existing check-out > requested check-in
        // =====================================================

        List<PropertyBooking> findByPropertyIdAndStatusAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                        Long propertyId,
                        String status,
                        LocalDate checkOutDate,
                        LocalDate checkInDate);

        // =====================================================
        // ADMIN STATISTICS
        // =====================================================

        long countByStatus(String status);
}