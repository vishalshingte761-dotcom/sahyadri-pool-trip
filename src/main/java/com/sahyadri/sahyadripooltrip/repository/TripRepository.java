package com.sahyadri.sahyadripooltrip.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sahyadri.sahyadripooltrip.entity.Trip;

import jakarta.persistence.LockModeType;

public interface TripRepository extends JpaRepository<Trip, Long> {

    // =====================================================
    // LOCK TRIP FOR BOOKING
    // Prevents two users from booking the same last seat
    // =====================================================

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Trip t WHERE t.tripId = :tripId")
    Optional<Trip> findByIdForUpdate(
            @Param("tripId") Long tripId
    );

    // =====================================================
    // ADVANCED TRIP SEARCH
    // =====================================================

    @Query("""
            SELECT t
            FROM Trip t
            WHERE UPPER(t.status) = 'ACTIVE'
            AND (:fromLocation IS NULL
                 OR LOWER(t.fromLocation) = LOWER(:fromLocation))
            AND (:toLocation IS NULL
                 OR LOWER(t.toLocation) = LOWER(:toLocation))
            AND (:travelDate IS NULL
                 OR t.travelDate = :travelDate)
            AND (:minPrice IS NULL
                 OR t.pricePerSeat >= :minPrice)
            AND (:maxPrice IS NULL
                 OR t.pricePerSeat <= :maxPrice)
            AND (:minSeats IS NULL
                 OR t.availableSeats >= :minSeats)
            """)
    List<Trip> advancedSearch(
            @Param("fromLocation") String fromLocation,
            @Param("toLocation") String toLocation,
            @Param("travelDate") LocalDate travelDate,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("minSeats") Integer minSeats
    );

    // =====================================================
    // BASIC TRIP SEARCH
    // =====================================================

    List<Trip>
    findByStatusAndAvailableSeatsGreaterThanAndFromLocationIgnoreCaseAndToLocationIgnoreCaseAndTravelDate(
            String status,
            int availableSeats,
            String fromLocation,
            String toLocation,
            LocalDate travelDate
    );

    // =====================================================
    // GET TRIPS BY STATUS
    // =====================================================

    List<Trip> findByStatus(String status);

    // =====================================================
    // GET DRIVER'S TRIPS
    // =====================================================

    List<Trip> findByDriverId(Long driverId);

    List<Trip> findByAgencyIdOrderByTravelDateAscDepartureTimeAsc(Long agencyId);

    List<Trip> findByAgencyVehicleId(Long agencyVehicleId);

    long countByAgencyId(Long agencyId);

    long countByAgencyIdAndStatus(Long agencyId, String status);

    // =====================================================
    // ADMIN STATISTICS
    // =====================================================

    long countByStatus(String status);
}