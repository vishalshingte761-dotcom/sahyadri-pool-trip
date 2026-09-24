package com.sahyadri.sahyadripooltrip.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sahyadri.sahyadripooltrip.entity.Fort;

@Repository
public interface FortRepository extends JpaRepository<Fort, String> {

    Optional<Fort> findByFortNameIgnoreCase(String fortName);

    List<Fort> findByDistrictIgnoreCase(String district);

    List<Fort> findByRecordStatusIgnoreCase(String recordStatus);

    List<Fort> findByVerificationStatusIgnoreCase(String verificationStatus);

    boolean existsByFortNameIgnoreCase(String fortName);
}