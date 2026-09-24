package com.sahyadri.sahyadripooltrip.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sahyadri.sahyadripooltrip.entity.Role;
import com.sahyadri.sahyadripooltrip.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    // =====================================================
    // ADMIN STATISTICS
    // =====================================================

    long countByRole(Role role);
}