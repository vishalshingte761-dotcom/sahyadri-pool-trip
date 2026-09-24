package com.sahyadri.sahyadripooltrip.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sahyadri.sahyadripooltrip.entity.Property;

import jakarta.persistence.LockModeType;

public interface PropertyRepository extends JpaRepository<Property, Long> {

     // =====================================================
     // ADVANCED PROPERTY SEARCH
     // =====================================================

     @Lock(LockModeType.PESSIMISTIC_WRITE)
     @Query("SELECT p FROM Property p WHERE p.propertyId = :propertyId")
     Optional<Property> findByIdForUpdate(@Param("propertyId") Long propertyId);

     @Query("""
               SELECT p
               FROM Property p
               WHERE UPPER(p.status) = 'ACTIVE'
               AND (:location IS NULL
                    OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%')))
               AND (:propertyType IS NULL
                    OR LOWER(p.propertyType) = LOWER(:propertyType))
               AND (:minPrice IS NULL
                    OR p.pricePerNight >= :minPrice)
               AND (:maxPrice IS NULL
                    OR p.pricePerNight <= :maxPrice)
               """)
     List<Property> advancedSearch(
               @Param("location") String location,
               @Param("propertyType") String propertyType,
               @Param("minPrice") Double minPrice,
               @Param("maxPrice") Double maxPrice);

     // =====================================================
     // GET PROPERTIES BY OWNER
     // =====================================================

     List<Property> findByOwnerId(Long ownerId);

     // =====================================================
     // GET PROPERTIES BY STATUS
     // =====================================================

     List<Property> findByStatus(String status);

     // =====================================================
     // COUNT PROPERTIES BY STATUS
     // =====================================================

     long countByStatus(String status);

     // =====================================================
     // SEARCH ACTIVE PROPERTIES BY LOCATION
     // =====================================================

     List<Property> findByStatusAndLocationIgnoreCase(
               String status,
               String location);

     // =====================================================
     // ADVANCED SEARCH - LOCATION + PRICE
     // =====================================================

     List<Property> findByStatusAndLocationIgnoreCaseAndPricePerNightBetween(
               String status,
               String location,
               Double minPrice,
               Double maxPrice);

     // =====================================================
     // SEARCH BY PRICE
     // =====================================================

     List<Property> findByStatusAndPricePerNightBetween(
               String status,
               Double minPrice,
               Double maxPrice);

     // =====================================================
     // SEARCH BY PROPERTY TYPE
     // =====================================================

     List<Property> findByStatusAndPropertyTypeIgnoreCase(
               String status,
               String propertyType);

     // =====================================================
     // SEARCH BY MINIMUM AVAILABLE ROOMS
     // =====================================================

     List<Property> findByStatusAndAvailableRoomsGreaterThanEqual(
               String status,
               Integer minRooms);
}