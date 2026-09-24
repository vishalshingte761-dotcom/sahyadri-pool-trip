package com.sahyadri.sahyadripooltrip.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sahyadri.sahyadripooltrip.entity.Role;
import com.sahyadri.sahyadripooltrip.entity.SosAlert;
import com.sahyadri.sahyadripooltrip.entity.SosStatus;
import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.repository.DriverProfileRepository;
import com.sahyadri.sahyadripooltrip.repository.SosAlertRepository;
import com.sahyadri.sahyadripooltrip.repository.UserRepository;

@RestController
@RequestMapping("/api/sos")
public class SosController {
    private final UserRepository users;
    private final DriverProfileRepository drivers;
    private final SosAlertRepository sos;

    public SosController(UserRepository u, DriverProfileRepository d, SosAlertRepository s) {
        users = u;
        drivers = d;
        sos = s;
    }

    private User me(Authentication a) {
        return users.findByEmail(a.getName()).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @PostMapping
    public ResponseEntity<?> trigger(
            @RequestBody Map<String, Object> body,
            Authentication auth) {

        User u = me(auth);

        // =====================================================
        // 1. PREVENT DUPLICATE ACTIVE SOS
        // =====================================================

        var existingActiveSos = sos.findFirstByUserIdAndStatusOrderByTriggeredAtDesc(
                u.getUserId(),
                SosStatus.ACTIVE);

        if (existingActiveSos.isPresent()) {

            return ResponseEntity.ok(
                    java.util.Map.of(
                            "success", false,
                            "message", "An active SOS alert already exists",
                            "sosId", existingActiveSos.get().getSosId()));
        }

        // =====================================================
        // 2. GET GPS
        // =====================================================

        Double latitude = num(body.get("latitude"));
        Double longitude = num(body.get("longitude"));
        Double accuracy = num(body.get("accuracy"));

        if (latitude == null || !Double.isFinite(latitude)) {
            throw new IllegalArgumentException(
                    "Valid latitude is required");
        }

        if (longitude == null || !Double.isFinite(longitude)) {
            throw new IllegalArgumentException(
                    "Valid longitude is required");
        }

        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90");
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException(
                    "Longitude must be between -180 and 180");
        }

        if (accuracy != null) {

            if (!Double.isFinite(accuracy)) {
                throw new IllegalArgumentException(
                        "Valid accuracy is required");
            }

            if (accuracy < 0) {
                throw new IllegalArgumentException(
                        "Accuracy cannot be negative");
            }
        }

        // =====================================================
        // 3. EMERGENCY CONTACT
        // =====================================================

        String emergencyName = null;
        String emergencyNumber = null;

        // -----------------------------------------------------
        // DRIVER
        // -----------------------------------------------------

        if (u.getRole() == Role.DRIVER) {

            var profile = drivers.findByUserId(u.getUserId()).orElse(null);

            if (profile != null) {
                emergencyName = profile.getEmergencyContactName();
                emergencyNumber = profile.getEmergencyContactNumber();
            } else {
                // Legacy driver accounts may predate driver_profiles.
                emergencyName = u.getEmergencyContactName();
                emergencyNumber = u.getEmergencyContactNumber();
            }
        }

        // -----------------------------------------------------
        // TRAVELER
        // -----------------------------------------------------

        else if (u.getRole() == Role.TRAVELER) {

            emergencyName = u.getEmergencyContactName();

            emergencyNumber = u.getEmergencyContactNumber();
        }

        // =====================================================
        // 4. EMERGENCY CONTACT REQUIRED
        // =====================================================

        if (emergencyName == null
                || emergencyName.isBlank()) {

            throw new IllegalArgumentException(
                    "Please add an emergency contact before using SOS");
        }

        if (emergencyNumber == null
                || emergencyNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Please add an emergency contact before using SOS");
        }

        // Extra safety validation
        if (!emergencyNumber.matches("^[6-9]\\d{9}$")) {

            throw new IllegalArgumentException(
                    "Emergency contact number is invalid");
        }

        // =====================================================
        // 5. CREATE SOS
        // =====================================================

        SosAlert s = new SosAlert();

        s.setUserId(u.getUserId());

        s.setEmergencyContactName(emergencyName);

        s.setEmergencyContactNumber(emergencyNumber);

        s.setLatitude(latitude);

        s.setLongitude(longitude);

        s.setAccuracy(accuracy);

        s.setTriggeredAt(LocalDateTime.now());

        s.setStatus(SosStatus.ACTIVE);

        sos.save(s);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(s);
    }

    private Double num(Object o) {

        if (o == null) {
            return null;
        }

        try {

            double value = Double.parseDouble(o.toString());

            if (!Double.isFinite(value)) {
                return null;
            }

            return value;

        } catch (Exception e) {

            return null;
        }
    }

    @GetMapping("/my")
    public List<SosAlert> my(Authentication a) {
        return sos.findByUserIdOrderByTriggeredAtDesc(me(a).getUserId());
    }

    @PostMapping("/{id}/cancel")
    public SosAlert cancel(@PathVariable Long id, Authentication a) {
        SosAlert s = sos.findById(id).orElseThrow(() -> new IllegalArgumentException("SOS not found"));
        if (!s.getUserId().equals(me(a).getUserId()))
            throw new IllegalArgumentException("Not your SOS alert");
        s.setStatus(SosStatus.CANCELLED);
        return sos.save(s);
    }

    @GetMapping("/admin")
    public List<SosAlert> admin() {
        return sos.findAll();
    }

    @PutMapping("/admin/{id}/resolve")
    public SosAlert resolve(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication a) {
        SosAlert s = sos.findById(id).orElseThrow(() -> new IllegalArgumentException("SOS not found"));
        s.setStatus(SosStatus.RESOLVED);
        s.setResolvedAt(LocalDateTime.now());
        s.setResolutionNote(body.get("resolutionNote"));
        return sos.save(s);
    }
}
