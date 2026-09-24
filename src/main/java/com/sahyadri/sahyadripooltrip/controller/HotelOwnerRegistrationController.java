package com.sahyadri.sahyadripooltrip.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.sahyadri.sahyadripooltrip.entity.HotelOwnerApplication;
import com.sahyadri.sahyadripooltrip.entity.Role;
import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.repository.HotelOwnerApplicationRepository;
import com.sahyadri.sahyadripooltrip.repository.UserRepository;

@RestController
@RequestMapping("/api/partners")
public class HotelOwnerRegistrationController {
    private final HotelOwnerApplicationRepository applications;
    private final UserRepository users;
    private final BCryptPasswordEncoder encoder;

    public HotelOwnerRegistrationController(
            HotelOwnerApplicationRepository applications,
            UserRepository users,
            BCryptPasswordEncoder encoder) {
        this.applications = applications;
        this.users = users;
        this.encoder = encoder;
    }

    @PostMapping("/hotel/register")
    @Transactional
    public ResponseEntity<?> register(@RequestBody HotelRegistrationRequest req) {
        require(req.ownerName(), "Owner / contact name");
        require(req.email(), "Email");
        require(req.phone(), "Mobile");
        require(req.password(), "Password");
        require(req.propertyName(), "Property / hotel name");
        require(req.propertyType(), "Property type");
        require(req.address(), "Address");
        require(req.cityDistrict(), "City / district");
        require(req.description(), "Property description");

        String email = req.email().trim().toLowerCase();
        String phone = req.phone().trim();
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
            throw new IllegalArgumentException("Enter a valid email address");
        if (!phone.matches("[6-9][0-9]{9}"))
            throw new IllegalArgumentException("Enter a valid 10-digit mobile number");
        if (req.password().length() < 8)
            throw new IllegalArgumentException("Password must be at least 8 characters");
        if (req.totalRooms() == null || req.totalRooms() < 1)
            throw new IllegalArgumentException("Total rooms must be at least 1");
        if (req.pricePerNight() == null || req.pricePerNight() < 0)
            throw new IllegalArgumentException("Starting price cannot be negative");

        if (users.existsByEmail(email) || applications.existsByEmailIgnoreCase(email))
            throw new IllegalArgumentException("Email is already registered or has an existing application");
        if (users.existsByPhone(phone) || applications.existsByPhone(phone))
            throw new IllegalArgumentException("Phone is already registered or has an existing application");

        HotelOwnerApplication a = new HotelOwnerApplication();
        a.setOwnerName(req.ownerName().trim());
        a.setEmail(email);
        a.setPhone(phone);
        a.setPasswordHash(encoder.encode(req.password()));
        a.setPropertyName(req.propertyName().trim());
        a.setPropertyType(req.propertyType().trim());
        a.setAddress(req.address().trim());
        a.setCityDistrict(req.cityDistrict().trim());
        a.setTotalRooms(req.totalRooms());
        a.setPricePerNight(req.pricePerNight());
        a.setDescription(req.description().trim());
        a.setStatus("PENDING");
        applications.save(a);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "status", "PENDING",
            "message", "Hotel / Stay Owner registration submitted successfully. It is pending admin verification."
        ));
    }

    private static void require(String value, String field) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(field + " is required");
    }

    public record HotelRegistrationRequest(
        String ownerName, String email, String phone, String password,
        String propertyName, String propertyType, String address,
        String cityDistrict, Integer totalRooms, Double pricePerNight,
        String description) {}
}
