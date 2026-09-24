package com.sahyadri.sahyadripooltrip.repository;

import com.sahyadri.sahyadripooltrip.entity.FortRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FortRouteRepository extends JpaRepository<FortRoute, String> {

    List<FortRoute> findByFortId(String fortId);

    Optional<FortRoute> findByRouteIdIgnoreCase(String routeId);

    List<FortRoute> findByRouteDifficultyIgnoreCase(String routeDifficulty);

    List<FortRoute> findByDifficultyIgnoreCase(String difficulty);

    List<FortRoute> findByRouteTypeIgnoreCase(String routeType);

    List<FortRoute> findByRouteVerificationStatusIgnoreCase(
            String routeVerificationStatus
    );

    List<FortRoute> findByGpsStatusIgnoreCase(String gpsStatus);

    List<FortRoute> findByTrekStatusIgnoreCase(String trekStatus);

    List<FortRoute> findByRouteConfidenceIgnoreCase(String routeConfidence);

    boolean existsByRouteIdIgnoreCase(String routeId);
}