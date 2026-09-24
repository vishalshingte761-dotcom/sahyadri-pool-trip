package com.sahyadri.sahyadripooltrip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.repository.UserRepository;

@RestController
@RequestMapping("/api/traveler")
public class TravelerController {

    private final UserRepository userRepository;

    public TravelerController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public User getProfile(Authentication authentication) {

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(()
                        -> new IllegalArgumentException("User not found"));
    }
    // =====================================================
// UPDATE TRAVELER PROFILE
// Traveler can update name and phone
// PUT /api/traveler/profile
// =====================================================

    @PutMapping("/profile")
    public ResponseEntity<?> updateTravelerProfile(
            @RequestBody User request,
            Authentication authentication) {

        String email = authentication.getName();

        User traveler = userRepository.findByEmail(email)
                .orElseThrow(()
                        -> new IllegalArgumentException("Traveler not found"));

        if (request.getName() != null && !request.getName().isBlank()) {
            traveler.setName(request.getName().trim());
        }

        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            traveler.setPhone(request.getPhone().trim());
        }

        User updatedTraveler = userRepository.save(traveler);

        return ResponseEntity.ok(updatedTraveler);
    }
}
