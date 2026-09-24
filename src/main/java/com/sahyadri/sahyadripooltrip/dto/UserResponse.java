package com.sahyadri.sahyadripooltrip.dto;

import com.sahyadri.sahyadripooltrip.entity.Role;

public record UserResponse(

                Long userId,
                String name,
                String email,
                String phone,
                Role role,
                String emergencyContactName,
                String emergencyContactNumber,
                String emergencyContactRelation) {

        // Backward-compatible constructor
        // Existing Admin/Driver code can continue using 5 fields.
        public UserResponse(
                        Long userId,
                        String name,
                        String email,
                        String phone,
                        Role role) {

                this(
                                userId,
                                name,
                                email,
                                phone,
                                role,
                                null,
                                null,
                                null);
        }
}