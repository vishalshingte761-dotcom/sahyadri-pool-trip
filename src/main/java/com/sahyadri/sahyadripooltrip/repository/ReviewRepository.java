package com.sahyadri.sahyadripooltrip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sahyadri.sahyadripooltrip.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByTravelerId(Long travelerId);

    List<Review> findByReviewTypeAndTargetId(
            String reviewType,
            Long targetId
    );

    boolean existsByTravelerIdAndReviewTypeAndTargetId(
            Long travelerId,
            String reviewType,
            Long targetId
    );

    @Query("""
            SELECT AVG(r.rating)
            FROM Review r
            WHERE r.reviewType = :reviewType
            AND r.targetId = :targetId
            """)
    Double findAverageRating(
            @Param("reviewType") String reviewType,
            @Param("targetId") Long targetId
    );
}