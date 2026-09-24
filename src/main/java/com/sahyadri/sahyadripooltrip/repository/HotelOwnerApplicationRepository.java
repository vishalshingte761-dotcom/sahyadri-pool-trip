package com.sahyadri.sahyadripooltrip.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sahyadri.sahyadripooltrip.entity.HotelOwnerApplication;

@Repository
public interface HotelOwnerApplicationRepository extends JpaRepository<HotelOwnerApplication, Long> {
    List<HotelOwnerApplication> findAllByOrderByCreatedAtDesc();
    Optional<HotelOwnerApplication> findByEmailIgnoreCase(String email);
    Optional<HotelOwnerApplication> findByPhone(String phone);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByPhone(String phone);
}
