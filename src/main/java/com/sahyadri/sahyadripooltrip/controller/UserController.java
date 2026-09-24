package com.sahyadri.sahyadripooltrip.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sahyadri.sahyadripooltrip.dto.UserResponse;
import com.sahyadri.sahyadripooltrip.dto.ChangePasswordRequest;
import com.sahyadri.sahyadripooltrip.dto.UpdateProfileRequest;
import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // =====================================================
    // GET MY PROFILE
    // GET /api/users/profile
    // =====================================================

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getMyProfile(
            Authentication authentication) {

        User user = userService.getCurrentUser(
                authentication.getName());

        return ResponseEntity.ok(
                new UserResponse(
                        user.getUserId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getRole(),
                        user.getEmergencyContactName(),
                        user.getEmergencyContactNumber(),
                        user.getEmergencyContactRelation()));
    }

    // =====================================================
    // UPDATE MY PROFILE
    // PUT /api/users/profile
    // =====================================================

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {

        User updatedUser = userService.updateProfile(
                authentication.getName(),
                request);

        return ResponseEntity.ok(
                new UserResponse(
                        updatedUser.getUserId(),
                        updatedUser.getName(),
                        updatedUser.getEmail(),
                        updatedUser.getPhone(),
                        updatedUser.getRole(),
                        updatedUser.getEmergencyContactName(),
                        updatedUser.getEmergencyContactNumber(),
                        updatedUser.getEmergencyContactRelation()));
    }

    // =====================================================
    // CHANGE PASSWORD
    // PUT /api/users/password
    // =====================================================

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        userService.changePassword(
                authentication.getName(),
                request);

        return ResponseEntity.ok(
                java.util.Map.of(
                        "success", true,
                        "message", "Password changed successfully"));
    }
}