package com.sahyadri.sahyadripooltrip.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sahyadri.sahyadripooltrip.entity.Role;
import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.repository.UserRepository;

@Configuration
public class AdminSeeder {

    @Bean
    CommandLineRunner createAdmin(
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder) {

        return args -> {

            String adminEmail = System.getenv("ADMIN_EMAIL");
            String adminPassword = System.getenv("ADMIN_PASSWORD");

            if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
                return;
            }

            if (userRepository.existsByEmail(adminEmail)) {
                System.out.println("ADMIN already exists.");
                return;
            }

            User admin = new User();

            admin.setName("Sahyadri Admin");
            admin.setEmail(adminEmail);
            admin.setPhone("9999999999");

            // Password is stored as BCrypt hash
            admin.setPasswordHash(
                    passwordEncoder.encode(adminPassword)
            );

            admin.setRole(Role.ADMIN);

            userRepository.save(admin);

            System.out.println("=================================");
            System.out.println("ADMIN CREATED SUCCESSFULLY");
            System.out.println("Email: " + adminEmail);
            System.out.println("=================================");
        };
    }
}