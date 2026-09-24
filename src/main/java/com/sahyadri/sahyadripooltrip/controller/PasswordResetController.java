package com.sahyadri.sahyadripooltrip.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sahyadri.sahyadripooltrip.dto.PasswordResetConfirmRequest;
import com.sahyadri.sahyadripooltrip.dto.PasswordResetStartRequest;
import com.sahyadri.sahyadripooltrip.dto.PasswordResetVerifyRequest;
import com.sahyadri.sahyadripooltrip.service.InputValidationService;
import com.sahyadri.sahyadripooltrip.service.PasswordResetService;

@RestController
@RequestMapping("/api/auth/password")
public class PasswordResetController {

    private final PasswordResetService service;
    private final InputValidationService validation;

    public PasswordResetController(
            PasswordResetService s,
            InputValidationService v) {

        service = s;
        validation = v;
    }

    @PostMapping("/forgot/email")
    public ResponseEntity<?> email(
            @RequestBody PasswordResetStartRequest r) {

        service.startEmail(
                validation.email(r.getIdentifier()));

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "OTP sent to your registered email"
                )
        );
    }

    @PostMapping("/forgot/phone")
    public ResponseEntity<?> phone(
            @RequestBody PasswordResetStartRequest r) {

        service.startPhone(
                validation.phone(r.getIdentifier(), true));

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "OTP sent to your registered phone"
                )
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(
            @RequestBody PasswordResetVerifyRequest r) {

        String resetToken = service.verify(
                r.getIdentifier(),
                r.getOtp()
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "resetToken", resetToken
                )
        );
    }

    @PostMapping("/reset")
    public ResponseEntity<?> reset(
            @RequestBody PasswordResetConfirmRequest r) {

        service.confirm(
                r.getResetToken(),
                r.getNewPassword()
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Password reset successful"
                )
        );
    }
}