package com.sahyadri.sahyadripooltrip.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sahyadri.sahyadripooltrip.dto.AuthResponse;
import com.sahyadri.sahyadripooltrip.dto.LoginRequest;
import com.sahyadri.sahyadripooltrip.dto.LoginResponse;
import com.sahyadri.sahyadripooltrip.dto.RegisterRequest;
import com.sahyadri.sahyadripooltrip.entity.Role;
import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.repository.UserRepository;
import com.sahyadri.sahyadripooltrip.repository.DriverProfileRepository;
import com.sahyadri.sahyadripooltrip.repository.AgencyRepository;
import com.sahyadri.sahyadripooltrip.repository.HotelOwnerApplicationRepository;
import com.sahyadri.sahyadripooltrip.entity.VerificationStatus;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final DriverProfileRepository driverProfileRepository;
    private final AgencyRepository agencyRepository;
    private final HotelOwnerApplicationRepository hotelOwnerApplicationRepository;
    private final InputValidationService validationService;

    // Constructor Injection
    public AuthService(
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder,
            JwtService jwtService, DriverProfileRepository driverProfileRepository,
            AgencyRepository agencyRepository, HotelOwnerApplicationRepository hotelOwnerApplicationRepository,
            InputValidationService validationService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.driverProfileRepository = driverProfileRepository;
        this.agencyRepository = agencyRepository;
        this.hotelOwnerApplicationRepository = hotelOwnerApplicationRepository;
        this.validationService = validationService;
    }

    // =========================
    // REGISTER
    // =========================
    public AuthResponse register(RegisterRequest request) {

        // Basic validation
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (request.getPassword() == null
                || request.getPassword().length() < 8) {

            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters"
            );
        }

        if (request.getRole() == null) {
            throw new IllegalArgumentException("Role is required");
        }

        // Privileged partner accounts must use document-verification registration.
        if (request.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Admin registration is not allowed");
        }
        if (request.getRole() == Role.DRIVER || request.getRole() == Role.AGENCY) {
            throw new IllegalArgumentException("Use the Driver/Agency document verification registration flow");
        }
        if (request.getRole() == Role.HOTEL_OWNER) {
            throw new IllegalArgumentException("Stay owner accounts require admin-approved onboarding");
        }

        String email = validationService.email(request.getEmail());

        // Check duplicate email
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        // Check duplicate phone
        String phone = validationService.phone(request.getPhone(), true);

        if (phone != null && userRepository.existsByPhone(phone)) {

            throw new IllegalArgumentException(
                    "Phone number is already registered"
            );
        }

        // Create user
        User user = new User();

        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPhone(phone);

        // NEVER store plain password
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);

        return new AuthResponse(
                "Registration successful",
                savedUser.getUserId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );
    }

    // =========================
    // LOGIN
    // =========================
    public LoginResponse login(LoginRequest request) {

        if (request.getEmail() == null
                || request.getEmail().isBlank()) {

            throw new IllegalArgumentException("Email is required");
        }

        if (request.getPassword() == null
                || request.getPassword().isBlank()) {

            throw new IllegalArgumentException("Password is required");
        }

        String email = validationService.email(request.getEmail());

        // Hotel Owner onboarding is controlled by the admin review flow.
        // Pending applications must wait; rejected applications must not be able to log in.
        var hotelApplication = hotelOwnerApplicationRepository.findByEmailIgnoreCase(email).orElse(null);
        if (hotelApplication != null) {
            String applicationStatus = hotelApplication.getStatus();
            if ("PENDING".equalsIgnoreCase(applicationStatus)) {
                throw new IllegalArgumentException("Hotel Owner application is pending admin approval.");
            }
            if ("REJECTED".equalsIgnoreCase(applicationStatus)) {
                throw new IllegalArgumentException("Hotel Owner application was rejected by admin. Please contact support or submit a new application.");
            }
        }

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(()
                        -> new IllegalArgumentException(
                        "Invalid email or password"
                )
                );

        if (user.getRole() == Role.DRIVER) {
            var profile = driverProfileRepository.findByUserId(user.getUserId()).orElse(null);
            if (profile != null && profile.getVerificationStatus() != VerificationStatus.APPROVED) {
                throw new IllegalArgumentException("Driver account is awaiting admin verification.");
            }
        }

        if (user.getRole() == Role.AGENCY) {
            var agency = agencyRepository.findByOwnerUserId(user.getUserId()).orElse(null);
            if (agency != null && agency.getVerificationStatus() != VerificationStatus.APPROVED) {
                throw new IllegalArgumentException("Agency account is awaiting admin document verification.");
            }
        }

        // Compare entered password with BCrypt hash
        boolean passwordMatches
                = passwordEncoder.matches(
                        request.getPassword(),
                        user.getPasswordHash()
                );

        if (!passwordMatches) {
            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }

        // Generate JWT
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );
        return new LoginResponse(
                "Login successful",
                token,
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

}
