package com.sahyadri.sahyadripooltrip.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class TripPricingService {
    public static final double PLATFORM_MARGIN_PERCENT = 10.0;
    public static final double SERVICE_FEE = 69.0;

    private record Rate(double perKm, int capacity, String category) {}

    private final Map<String, Rate> rates = new LinkedHashMap<>();

    public TripPricingService() {
        add("WagonR / Celerio", 11, 4, "Economy");
        add("Swift Dzire", 12, 4, "Economy");
        add("Hyundai Aura", 12, 4, "Economy");
        add("Honda Amaze", 12, 4, "Economy");
        add("Honda City", 14, 4, "Premium Sedan");
        add("Maruti Ciaz", 14, 4, "Premium Sedan");
        add("Hyundai Verna", 15, 4, "Premium Sedan");
        add("Ertiga / Rumion", 16, 6, "MUV");
        add("Kia Carens", 17, 6, "MUV");
        add("Toyota Innova", 18, 7, "SUV");
        add("Innova Crysta", 20, 7, "Premium SUV");
        add("Toyota Innova Hycross", 22, 7, "Premium SUV");
        add("Toyota Fortuner", 25, 7, "Luxury SUV");
        add("Toyota Camry", 28, 4, "Luxury");
        add("Toyota Vellfire", 45, 6, "Luxury");
        add("Kia Carnival", 30, 7, "Luxury MPV");
        add("MG Gloster", 27, 6, "Luxury SUV");
        add("Volvo XC90", 45, 6, "Luxury SUV");
        add("Mercedes-Benz E-Class", 55, 4, "Luxury");
        add("BMW 5 Series", 55, 4, "Luxury");
        add("Audi A6", 55, 4, "Luxury");
        add("Mercedes-Benz V-Class", 60, 7, "Luxury Van");
        add("Tempo Traveller 12", 25, 12, "Traveller");
        add("Tempo Traveller 14", 27, 14, "Traveller");
        add("Tempo Traveller 17", 29, 17, "Traveller");
        add("Tempo Traveller 20", 31, 20, "Traveller");
        add("Tempo Traveller 26", 35, 26, "Traveller");
        add("Force Urbania 17", 35, 17, "Premium Traveller");
        add("Mini Bus 20", 30, 20, "Bus");
        add("Bus 27", 32, 27, "Bus");
        add("Bus 35", 35, 35, "Bus");
        add("Bus 41", 38, 41, "Bus");
        add("Bus 45", 40, 45, "Bus");
        add("Bus 50", 43, 50, "Bus");
        add("Bus 52", 45, 52, "Bus");
        add("Bus 56", 48, 56, "Bus");
    }
    private void add(String name,double rate,int seats,String category){ rates.put(name,new Rate(rate,seats,category)); }
    public double rateFor(String vehicleType){
        if(vehicleType==null || vehicleType.isBlank()) return 16.0;
        Rate r=rates.get(vehicleType.trim());
        if(r!=null) return r.perKm();
        String v=vehicleType.toLowerCase();
        if(v.contains("sedan")) return 13;
        if(v.contains("hatch")) return 11;
        if(v.contains("tempo") || v.contains("traveller")) return 27;
        if(v.contains("bus")) return 35;
        if(v.contains("suv") || v.contains("muv")) return 18;
        return 16;
    }
    public double serviceFee(){ return SERVICE_FEE; }
    public double platformFee(double base){ return round(base * PLATFORM_MARGIN_PERCENT / 100.0); }
    public double round(double n){ return Math.round(n*100.0)/100.0; }
    public Map<String,Object> calculate(String vehicleType, int seats, double oneWayDistanceKm, boolean returnTrip){
        if(seats<1) throw new IllegalArgumentException("Passenger capacity must be at least 1");
        if(oneWayDistanceKm<=0) throw new IllegalArgumentException("Route distance must be greater than 0");
        double distance=returnTrip ? oneWayDistanceKm*2.0 : oneWayDistanceKm;
        double rate=rateFor(vehicleType);
        double base=round(distance*rate);
        double margin=platformFee(base);
        double service=SERVICE_FEE;
        double total=round(base+margin+service);
        double perPerson=round((base+margin)/seats);
        Map<String,Object> out=new LinkedHashMap<>();
        out.put("vehicleType", vehicleType); out.put("seats", seats);
        out.put("oneWayDistanceKm", round(oneWayDistanceKm)); out.put("calculatedDistanceKm", round(distance));
        out.put("returnTrip", returnTrip); out.put("platformMarginPercent", PLATFORM_MARGIN_PERCENT);
        out.put("baseTripFare", base); out.put("platformFee", margin); out.put("serviceFee", service);
        out.put("totalTripFare", total); out.put("pricePerPerson", perPerson);
        return out;
    }
    public List<Map<String,Object>> vehicleCatalog(){
        return rates.entrySet().stream().map(e->{ Map<String,Object> m=new LinkedHashMap<>(); m.put("vehicleType",e.getKey()); m.put("category",e.getValue().category()); m.put("capacity",e.getValue().capacity()); return m; }).toList();
    }

    public List<Map<String,Object>> rateCard(){
        return rates.entrySet().stream().map(e->{ Map<String,Object> m=new LinkedHashMap<>(); m.put("vehicleType",e.getKey()); m.put("category",e.getValue().category()); m.put("capacity",e.getValue().capacity()); m.put("ratePerKm",e.getValue().perKm()); return m; }).toList();
    }
}
