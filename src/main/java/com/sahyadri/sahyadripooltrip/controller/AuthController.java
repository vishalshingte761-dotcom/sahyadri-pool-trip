package com.sahyadri.sahyadripooltrip.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import com.sahyadri.sahyadripooltrip.dto.AuthResponse;
import com.sahyadri.sahyadripooltrip.dto.LoginRequest;
import com.sahyadri.sahyadripooltrip.dto.LoginResponse;
import com.sahyadri.sahyadripooltrip.dto.RegisterRequest;
import com.sahyadri.sahyadripooltrip.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
   public ResponseEntity<LoginResponse> login(
        @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
        @Valid @RequestBody RegisterRequest request){

        AuthResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}
