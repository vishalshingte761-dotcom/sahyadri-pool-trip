package com.sahyadri.sahyadripooltrip.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sahyadri.sahyadripooltrip.dto.ChangePasswordRequest;
import com.sahyadri.sahyadripooltrip.dto.UpdateProfileRequest;
import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =====================================================
    // GET CURRENT USER
    // =====================================================

    public User getCurrentUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found"));
    }

    // =====================================================
    // UPDATE PROFILE
    // =====================================================

    public User updateProfile(
            String email,
            UpdateProfileRequest request) {

        User user = getCurrentUser(email);

        // -------------------------------------------------
        // PHONE
        // -------------------------------------------------

        String newPhone = request.getPhone();

        if (newPhone != null) {
            newPhone = newPhone.trim();

            if (newPhone.isBlank()) {
                newPhone = null;
            }
        }

        // Check duplicate phone
        if (newPhone != null
                && !newPhone.equals(user.getPhone())
                && userRepository.existsByPhone(newPhone)) {

            throw new IllegalArgumentException(
                    "Phone number is already registered");
        }

        // -------------------------------------------------
        // BASIC PROFILE
        // -------------------------------------------------

        user.setName(request.getName().trim());
        user.setPhone(newPhone);

        // -------------------------------------------------
        // EMERGENCY CONTACT
        // -------------------------------------------------

        String emergencyName = request.getEmergencyContactName();
        String emergencyNumber = request.getEmergencyContactNumber();
        String emergencyRelation = request.getEmergencyContactRelation();

        if (emergencyName != null) {
            emergencyName = emergencyName.trim();

            if (emergencyName.isBlank()) {
                emergencyName = null;
            }
        }

        if (emergencyNumber != null) {
            emergencyNumber = emergencyNumber.trim();

            if (emergencyNumber.isBlank()) {
                emergencyNumber = null;
            }
        }

        if (emergencyRelation != null) {
            emergencyRelation = emergencyRelation.trim();

            if (emergencyRelation.isBlank()) {
                emergencyRelation = null;
            }
        }

        // -------------------------------------------------
        // EMERGENCY CONTACT VALIDATION
        // -------------------------------------------------

        boolean anyEmergencyFieldProvided =
                emergencyName != null
                || emergencyNumber != null
                || emergencyRelation != null;

        if (anyEmergencyFieldProvided) {

            if (emergencyName == null) {
                throw new IllegalArgumentException(
                        "Emergency contact name is required");
            }

            if (emergencyNumber == null) {
                throw new IllegalArgumentException(
                        "Emergency contact number is required");
            }

            // Valid Indian mobile number
            if (!emergencyNumber.matches("^[6-9]\\d{9}$")) {
                throw new IllegalArgumentException(
                        "Invalid emergency contact number");
            }

            if (emergencyRelation == null) {
                throw new IllegalArgumentException(
                        "Emergency contact relation is required");
            }
        }

        // -------------------------------------------------
        // SAVE EMERGENCY CONTACT
        // -------------------------------------------------

        user.setEmergencyContactName(emergencyName);
        user.setEmergencyContactNumber(emergencyNumber);
        user.setEmergencyContactRelation(emergencyRelation);

        // IMPORTANT: return updated user
        return userRepository.save(user);
    }

    // =====================================================
    // CHANGE PASSWORD
    // =====================================================

    public void changePassword(
            String email,
            ChangePasswordRequest request) {

        User user = getCurrentUser(email);

        // Verify current password
        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPasswordHash())) {

            throw new IllegalArgumentException(
                    "Current password is incorrect");
        }

        // Prevent same password
        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPasswordHash())) {

            throw new IllegalArgumentException(
                    "New password must be different from current password");
        }

        user.setPasswordHash(
                passwordEncoder.encode(
                        request.getNewPassword()));

        userRepository.save(user);
    }
}