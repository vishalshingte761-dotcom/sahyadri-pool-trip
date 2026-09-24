package com.sahyadri.sahyadripooltrip.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sahyadri.sahyadripooltrip.entity.Agency;
import com.sahyadri.sahyadripooltrip.entity.DriverProfile;
import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.repository.AgencyRepository;
import com.sahyadri.sahyadripooltrip.repository.DriverProfileRepository;
import com.sahyadri.sahyadripooltrip.repository.UserRepository;
import com.sahyadri.sahyadripooltrip.service.VehicleOptionsService;

@RestController
@RequestMapping("/api/partner")
public class PartnerController {
    private final UserRepository users;
    private final DriverProfileRepository drivers;
    private final AgencyRepository agencies;
    private final VehicleOptionsService vehicleOptionsService;

    public PartnerController(
            UserRepository u,
            DriverProfileRepository d,
            AgencyRepository a,
            VehicleOptionsService vehicleOptionsService) {

        users = u;
        drivers = d;
        agencies = a;
        this.vehicleOptionsService = vehicleOptionsService;
    }

    private User me(Authentication a) {
        return users.findByEmail(a.getName()).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @GetMapping("/driver/profile")
    public DriverProfile driverProfile(Authentication a) {
        return drivers.findByUserId(me(a).getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Driver profile not found"));
    }

    @GetMapping("/agency/profile")
    public Agency agencyProfile(Authentication a) {
        return agencies.findByOwnerUserId(me(a).getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Agency profile not found"));
    }

    @GetMapping("/vehicle-options")
public Map<String, List<Integer>> vehicleOptions() {
    return vehicleOptionsService.getOptions();
}
}
