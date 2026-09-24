package com.sahyadri.sahyadripooltrip.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sahyadri.sahyadripooltrip.entity.AgencyVehicle;

public interface AgencyVehicleRepository extends JpaRepository<AgencyVehicle, Long> {
    List<AgencyVehicle> findByAgencyIdOrderByCreatedAtDesc(Long agencyId);
    boolean existsByRegistrationNumberIgnoreCase(String registrationNumber);
}
