package com.sahyadri.sahyadripooltrip.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sahyadri.sahyadripooltrip.dto.CreateReviewRequest;
import com.sahyadri.sahyadripooltrip.dto.ReviewResponse;
import com.sahyadri.sahyadripooltrip.entity.Property;
import com.sahyadri.sahyadripooltrip.entity.Review;
import com.sahyadri.sahyadripooltrip.entity.Trip;
import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.repository.BookingRepository;
import com.sahyadri.sahyadripooltrip.repository.PropertyBookingRepository;
import com.sahyadri.sahyadripooltrip.repository.PropertyRepository;
import com.sahyadri.sahyadripooltrip.repository.ReviewRepository;
import com.sahyadri.sahyadripooltrip.repository.TripRepository;
import com.sahyadri.sahyadripooltrip.repository.UserRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

        private final ReviewRepository reviewRepository;
        private final UserRepository userRepository;
        private final TripRepository tripRepository;
        private final BookingRepository bookingRepository;
        private final PropertyRepository propertyRepository;
        private final PropertyBookingRepository propertyBookingRepository;

        public ReviewController(
                        ReviewRepository reviewRepository,
                        UserRepository userRepository,
                        TripRepository tripRepository,
                        BookingRepository bookingRepository,
                        PropertyRepository propertyRepository,
                        PropertyBookingRepository propertyBookingRepository) {

                this.reviewRepository = reviewRepository;
                this.userRepository = userRepository;
                this.tripRepository = tripRepository;
                this.bookingRepository = bookingRepository;
                this.propertyRepository = propertyRepository;
                this.propertyBookingRepository = propertyBookingRepository;
        }

        // =====================================================
        // CREATE REVIEW
        // POST /api/reviews
        // =====================================================

        @PostMapping
        public ResponseEntity<ReviewResponse> createReview(
                        @Valid @RequestBody CreateReviewRequest request,
                        Authentication authentication) {

                String email = authentication.getName();

                User traveler = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("Traveler not found"));

                if (request.getReviewType() == null
                                || request.getReviewType().isBlank()) {

                        throw new IllegalArgumentException(
                                        "Review type is required");
                }

                String reviewType = request.getReviewType().trim().toUpperCase();

                if (!reviewType.equals("TRIP")
                                && !reviewType.equals("PROPERTY")) {

                        throw new IllegalArgumentException(
                                        "Review type must be TRIP or PROPERTY");
                }

                if (request.getTargetId() == null) {

                        throw new IllegalArgumentException(
                                        "Target ID is required");
                }

                if (request.getRating() == null
                                || request.getRating() < 1
                                || request.getRating() > 5) {

                        throw new IllegalArgumentException(
                                        "Rating must be between 1 and 5");
                }

                // =====================================================
                // PREVENT DUPLICATE REVIEW
                // =====================================================

                boolean alreadyReviewed = reviewRepository
                                .existsByTravelerIdAndReviewTypeAndTargetId(
                                                traveler.getUserId(),
                                                reviewType,
                                                request.getTargetId());

                if (alreadyReviewed) {

                        throw new IllegalArgumentException(
                                        "You have already reviewed this target");
                }

                // =====================================================
                // TRIP REVIEW
                // =====================================================

                if (reviewType.equals("TRIP")) {

                        Trip trip = tripRepository
                                        .findById(request.getTargetId())
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Trip not found"));

                        boolean hasConfirmedBooking = bookingRepository
                                        .existsByTripIdAndTravelerIdAndStatus(
                                                        trip.getTripId(),
                                                        traveler.getUserId(),
                                                        "CONFIRMED");

                        if (!hasConfirmedBooking) {

                                throw new IllegalArgumentException(
                                                "You can review only a trip with a confirmed booking");
                        }
                }

                // =====================================================
                // PROPERTY REVIEW
                // =====================================================B

                if (reviewType.equals("PROPERTY")) {

                        Property property = propertyRepository
                                        .findById(request.getTargetId())
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Property not found"));

                        boolean hasConfirmedBooking = propertyBookingRepository
                                        .existsByPropertyIdAndTravelerIdAndStatus(
                                                        property.getPropertyId(),
                                                        traveler.getUserId(),
                                                        "CONFIRMED");

                        if (!hasConfirmedBooking) {

                                throw new IllegalArgumentException(
                                                "You can review only a property with a confirmed booking");
                        }
                }

                // =====================================================
                // CREATE REVIEW
                // =====================================================

                Review review = new Review();

                review.setTravelerId(traveler.getUserId());
                review.setReviewType(reviewType);
                review.setTargetId(request.getTargetId());
                review.setRating(request.getRating());
                review.setComment(request.getComment());

                Review savedReview = reviewRepository.save(review);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(toResponse(savedReview));
        }

        // =====================================================
        // GET MY REVIEWS
        // GET /api/reviews/my
        // =====================================================

        @GetMapping("/my")
        public ResponseEntity<List<ReviewResponse>> getMyReviews(
                        Authentication authentication) {

                String email = authentication.getName();

                User traveler = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Traveler not found"));

                List<ReviewResponse> response = reviewRepository
                                .findByTravelerId(traveler.getUserId())
                                .stream()
                                .map(this::toResponse)
                                .toList();

                return ResponseEntity.ok(response);
        }

        // =====================================================
        // GET REVIEWS FOR TARGET
        // GET /api/reviews/{reviewType}/{targetId}
        // =====================================================

        @GetMapping("/{reviewType}/{targetId}")
        public ResponseEntity<List<ReviewResponse>> getTargetReviews(
                        @PathVariable String reviewType,
                        @PathVariable Long targetId) {

                String type = reviewType.trim().toUpperCase();

                validateReviewType(type);

                List<ReviewResponse> response = reviewRepository
                                .findByReviewTypeAndTargetId(
                                                type,
                                                targetId)
                                .stream()
                                .map(this::toResponse)
                                .toList();

                return ResponseEntity.ok(response);
        }

        // =====================================================
        // GET AVERAGE RATING
        // GET /api/reviews/rating/{reviewType}/{targetId}
        // =====================================================

        @GetMapping("/rating/{reviewType}/{targetId}")
        public ResponseEntity<?> getAverageRating(
                        @PathVariable String reviewType,
                        @PathVariable Long targetId) {

                String type = reviewType.trim().toUpperCase();

                validateReviewType(type);

                Double averageRating = reviewRepository.findAverageRating(
                                type,
                                targetId);

                if (averageRating == null) {
                        averageRating = 0.0;
                }

                return ResponseEntity.ok(
                                java.util.Map.of(
                                                "reviewType", type,
                                                "targetId", targetId,
                                                "averageRating", averageRating));
        }

        // =====================================================
        // DELETE MY REVIEW
        // DELETE /api/reviews/{reviewId}
        // =====================================================

        @DeleteMapping("/{reviewId}")
        public ResponseEntity<ReviewResponse> deleteReview(
                        @PathVariable Long reviewId,
                        Authentication authentication) {

                String email = authentication.getName();

                User traveler = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Traveler not found"));

                Review review = reviewRepository.findById(reviewId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Review not found"));

                if (!review.getTravelerId()
                                .equals(traveler.getUserId())) {

                        throw new IllegalArgumentException(
                                        "You can delete only your own review");
                }

                ReviewResponse response = toResponse(review);

                reviewRepository.delete(review);

                return ResponseEntity.ok(response);
        }

        // =====================================================
        // VALIDATE REVIEW TYPE
        // =====================================================

        private void validateReviewType(String type) {

                if (!type.equals("TRIP")
                                && !type.equals("PROPERTY")) {

                        throw new IllegalArgumentException(
                                        "Review type must be TRIP or PROPERTY");
                }
        }

        // =====================================================
        // ENTITY -> RESPONSE DTO
        // =====================================================

        private ReviewResponse toResponse(Review review) {

                ReviewResponse response = new ReviewResponse();

                response.setReviewId(
                                review.getReviewId());

                response.setTravelerId(
                                review.getTravelerId());

                response.setReviewType(
                                review.getReviewType());

                response.setTargetId(
                                review.getTargetId());

                response.setRating(
                                review.getRating());

                response.setComment(
                                review.getComment());

                response.setCreatedAt(
                                review.getCreatedAt());

                return response;
        }
}