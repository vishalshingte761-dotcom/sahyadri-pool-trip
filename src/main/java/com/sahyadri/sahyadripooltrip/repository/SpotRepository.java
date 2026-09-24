package com.sahyadri.sahyadripooltrip.repository;

import com.sahyadri.sahyadripooltrip.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpotRepository extends JpaRepository<Spot, String> {
    Optional<Spot> findByNameIgnoreCase(String name);
    List<Spot> findByCategoryIgnoreCase(String category);
    List<Spot> findByDistrictIgnoreCase(String district);
    List<Spot> findByRecordStatusIgnoreCase(String recordStatus);
    List<Spot> findByVerificationStatusIgnoreCase(String verificationStatus);
}
