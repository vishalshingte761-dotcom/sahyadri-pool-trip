package com.sahyadri.sahyadripooltrip.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class VehicleOptionsService {

    private final Map<String, List<Integer>> options;

    public VehicleOptionsService() {

        Map<String, List<Integer>> m = new LinkedHashMap<>();
        m.put("WagonR / Celerio", List.of(4));
        m.put("Swift Dzire", List.of(4));
        m.put("Hyundai Aura", List.of(4));
        m.put("Honda Amaze", List.of(4));
        m.put("Honda City", List.of(4));
        m.put("Maruti Ciaz", List.of(4));
        m.put("Hyundai Verna", List.of(4));
        m.put("Ertiga / Rumion", List.of(6));
        m.put("Kia Carens", List.of(6));
        m.put("Toyota Innova", List.of(7));
        m.put("Innova Crysta", List.of(7));
        m.put("Toyota Innova Hycross", List.of(7));
        m.put("Toyota Fortuner", List.of(7));
        m.put("Toyota Camry", List.of(4));
        m.put("Toyota Vellfire", List.of(6));
        m.put("Kia Carnival", List.of(7));
        m.put("MG Gloster", List.of(6));
        m.put("Volvo XC90", List.of(6));
        m.put("Mercedes-Benz E-Class", List.of(4));
        m.put("BMW 5 Series", List.of(4));
        m.put("Audi A6", List.of(4));
        m.put("Mercedes-Benz V-Class", List.of(7));
        m.put("Tempo Traveller 12", List.of(12));
        m.put("Tempo Traveller 14", List.of(14));
        m.put("Tempo Traveller 17", List.of(17));
        m.put("Tempo Traveller 20", List.of(20));
        m.put("Tempo Traveller 26", List.of(26));
        m.put("Force Urbania 17", List.of(17));
        m.put("Mini Bus 20", List.of(20));
        m.put("Bus 27", List.of(27));
        m.put("Bus 35", List.of(35));
        m.put("Bus 41", List.of(41));
        m.put("Bus 45", List.of(45));
        m.put("Bus 50", List.of(50));
        m.put("Bus 52", List.of(52));
        m.put("Bus 56", List.of(56));
        this.options = Map.copyOf(m);
    }

    public Map<String, List<Integer>> getOptions() {
        return options;
    }

    public boolean isValid(String vehicleType, Integer seatingCapacity) {

        if (vehicleType == null || vehicleType.isBlank()) {
            return false;
        }

        if (seatingCapacity == null) {
            return false;
        }

        List<Integer> allowedSeats = options.get(vehicleType.trim());

        if (allowedSeats == null) {
            return false;
        }

        return allowedSeats.contains(seatingCapacity);
    }

    public void validate(String vehicleType, Integer seatingCapacity) {

        if (vehicleType == null || vehicleType.isBlank()) {
            throw new IllegalArgumentException("Vehicle type is required");
        }

        if (seatingCapacity == null) {
            throw new IllegalArgumentException("Seating capacity is required");
        }

        String type = vehicleType.trim();

        List<Integer> allowedSeats = options.get(type);

        if (allowedSeats == null) {
            throw new IllegalArgumentException(
                    "Invalid vehicle type: " + type);
        }

        if (!allowedSeats.contains(seatingCapacity)) {
            throw new IllegalArgumentException(
                    "Invalid seating capacity " + seatingCapacity
                            + " for " + type
                            + ". Allowed seats: " + allowedSeats);
        }
    }
}