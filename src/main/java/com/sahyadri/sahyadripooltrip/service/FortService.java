package com.sahyadri.sahyadripooltrip.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sahyadri.sahyadripooltrip.entity.Fort;
import com.sahyadri.sahyadripooltrip.entity.FortRoute;
import com.sahyadri.sahyadripooltrip.repository.FortRepository;
import com.sahyadri.sahyadripooltrip.repository.FortRouteRepository;

@Service
@Transactional
public class FortService {

    private final FortRepository fortRepository;
    private final FortRouteRepository fortRouteRepository;

    public List<FortRoute> getRoutesByDifficulty(String routeDifficulty) {
        return fortRouteRepository.findByRouteDifficultyIgnoreCase(routeDifficulty);
    }

    public FortService(
            FortRepository fortRepository,
            FortRouteRepository fortRouteRepository) {
        this.fortRepository = fortRepository;
        this.fortRouteRepository = fortRouteRepository;
    }

    public List<Fort> getAllForts() {
        return fortRepository.findAll();
    }

    public Optional<Fort> getFortById(String fortId) {
        return fortRepository.findById(fortId);
    }

    public Optional<Fort> getFortByName(String fortName) {
        return fortRepository.findByFortNameIgnoreCase(fortName);
    }

    public List<Fort> getFortsByDistrict(String district) {
        return fortRepository.findByDistrictIgnoreCase(district);
    }

    public List<Fort> getFortsByRecordStatus(String recordStatus) {
        return fortRepository.findByRecordStatusIgnoreCase(recordStatus);
    }

    public List<Fort> getFortsByVerificationStatus(String verificationStatus) {
        return fortRepository.findByVerificationStatusIgnoreCase(verificationStatus);
    }

    public List<FortRoute> getRoutesByFortId(String fortId) {
        return fortRouteRepository.findByFortId(fortId);
    }

    public Optional<FortRoute> getRouteById(String routeId) {
        return fortRouteRepository.findById(routeId);
    }

    public Fort saveFort(Fort fort) {
        return fortRepository.save(fort);
    }

    public FortRoute saveRoute(FortRoute route) {
        return fortRouteRepository.save(route);
    }

    public boolean fortExists(String fortId) {
        return fortRepository.existsById(fortId);
    }

    public boolean fortNameExists(String fortName) {
        return fortRepository.existsByFortNameIgnoreCase(fortName);
    }

    public boolean routeExists(String routeId) {
        return fortRouteRepository.existsByRouteIdIgnoreCase(routeId);
    }

    public void deleteFort(String fortId) {
        List<FortRoute> routes = fortRouteRepository.findByFortId(fortId);

        if (!routes.isEmpty()) {
            fortRouteRepository.deleteAll(routes);
        }

        fortRepository.deleteById(fortId);
    }

    public long getFortCount() {
        return fortRepository.count();
    }

    public long getRouteCount() {
        return fortRouteRepository.count();
    }
}