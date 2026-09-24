package com.sahyadri.sahyadripooltrip.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.sahyadri.sahyadripooltrip.entity.HotelOwnerApplication;
import com.sahyadri.sahyadripooltrip.entity.Role;
import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.repository.HotelOwnerApplicationRepository;
import com.sahyadri.sahyadripooltrip.repository.UserRepository;

@RestController
@RequestMapping("/api/admin/hotel-applications")
public class AdminHotelApplicationController {
    private final HotelOwnerApplicationRepository applications;
    private final UserRepository users;
    private final BCryptPasswordEncoder encoder;

    public AdminHotelApplicationController(
            HotelOwnerApplicationRepository applications,
            UserRepository users,
            BCryptPasswordEncoder encoder) {
        this.applications = applications;
        this.users = users;
        this.encoder = encoder;
    }

    @GetMapping
    public List<HotelOwnerApplication> all() {
        return applications.findAllByOrderByCreatedAtDesc();
    }

    @PutMapping("/{id}/review")
    @Transactional
    public ResponseEntity<?> review(
            @PathVariable Long id,
            @RequestBody ReviewRequest request,
            Authentication authentication) {

        HotelOwnerApplication a = applications.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hotel application not found"));

        String status = request.status() == null ? "" : request.status().trim().toUpperCase();
        if (!status.equals("APPROVED") && !status.equals("REJECTED"))
            throw new IllegalArgumentException("Status must be APPROVED or REJECTED");

        // Only PENDING applications can be reviewed. An approved account cannot be
        // accidentally changed back to REJECTED (and vice versa).
        if (!"PENDING".equalsIgnoreCase(a.getStatus()))
            throw new IllegalArgumentException("This hotel application has already been reviewed: " + a.getStatus());

        if (status.equals("APPROVED")) {
            if (users.existsByEmail(a.getEmail()) || users.existsByPhone(a.getPhone()))
                throw new IllegalArgumentException("A user with this email or phone already exists");

            User user = new User();
            user.setName(a.getOwnerName());
            user.setEmail(a.getEmail());
            user.setPhone(a.getPhone());
            user.setPasswordHash(a.getPasswordHash());
            user.setRole(Role.HOTEL_OWNER);
            users.save(user);
        }

        a.setStatus(status);
        a.setReviewedAt(LocalDateTime.now());
        a.setReviewedBy(authentication != null ? authentication.getName() : "ADMIN");
        applications.save(a);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "status", status,
            "message", status.equals("APPROVED")
                ? "Hotel Owner application approved and account created."
                : "Hotel Owner application rejected."
        ));
    }

    public record ReviewRequest(String status) {}
}
