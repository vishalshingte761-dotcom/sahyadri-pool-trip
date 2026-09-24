package com.sahyadri.sahyadripooltrip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sahyadri.sahyadripooltrip.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // =====================================================
    // GET BOOKINGS BY TRAVELER
    // =====================================================

    List<Booking> findByTravelerId(Long travelerId);

    // =====================================================
    // GET BOOKINGS BY TRIP
    // =====================================================

    List<Booking> findByTripId(Long tripId);

    List<Booking> findByTripIdIn(List<Long> tripIds);

    // =====================================================
    // GET BOOKINGS BY TRIP AND STATUS
    // =====================================================

    List<Booking> findByTripIdAndStatus(
            Long tripId,
            String status
    );

    // =====================================================
    // CHECK BOOKING
    // =====================================================

    boolean existsByTripIdAndTravelerId(
            Long tripId,
            Long travelerId
    );

    // =====================================================
    // CHECK CONFIRMED BOOKING
    // =====================================================

    boolean existsByTripIdAndTravelerIdAndStatus(
            Long tripId,
            Long travelerId,
            String status
    );

    // =====================================================
    // ADMIN STATISTICS
    // =====================================================

    long countByStatus(String status);
}