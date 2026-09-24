package com.sahyadri.sahyadripooltrip.controller;

import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.sahyadri.sahyadripooltrip.dto.CreateAgencyTripRequest;
import com.sahyadri.sahyadripooltrip.dto.CreateAgencyVehicleRequest;
import com.sahyadri.sahyadripooltrip.entity.Agency;
import com.sahyadri.sahyadripooltrip.entity.AgencyVehicle;
import com.sahyadri.sahyadripooltrip.entity.Booking;
import com.sahyadri.sahyadripooltrip.entity.Trip;
import com.sahyadri.sahyadripooltrip.entity.VerificationStatus;
import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.repository.AgencyRepository;
import com.sahyadri.sahyadripooltrip.repository.AgencyVehicleRepository;
import com.sahyadri.sahyadripooltrip.repository.BookingRepository;
import com.sahyadri.sahyadripooltrip.repository.TripRepository;
import com.sahyadri.sahyadripooltrip.repository.UserRepository;
import com.sahyadri.sahyadripooltrip.service.TripPricingService;
import com.sahyadri.sahyadripooltrip.service.VehicleOptionsService;

@RestController
@RequestMapping("/api/agency")
@PreAuthorize("hasRole('AGENCY')")
public class AgencyController {
    private final UserRepository users; private final AgencyRepository agencies;
    private final AgencyVehicleRepository vehicles; private final TripRepository trips;
    private final BookingRepository bookings; private final VehicleOptionsService vehicleOptions;
    private final TripPricingService pricing;
    public AgencyController(UserRepository users, AgencyRepository agencies, AgencyVehicleRepository vehicles,
            TripRepository trips, BookingRepository bookings, VehicleOptionsService vehicleOptions, TripPricingService pricing) {
        this.users=users; this.agencies=agencies; this.vehicles=vehicles; this.trips=trips; this.bookings=bookings; this.vehicleOptions=vehicleOptions; this.pricing=pricing;
    }
    private Agency myAgency(Authentication auth) {
        User user=users.findByEmail(auth.getName()).orElseThrow(()->new IllegalArgumentException("User not found"));
        Agency agency=agencies.findByOwnerUserId(user.getUserId()).orElseThrow(()->new IllegalArgumentException("Agency profile not found"));
        if(agency.getVerificationStatus()!=VerificationStatus.APPROVED) throw new IllegalArgumentException("Agency verification is pending. Admin approval is required before publishing trips.");
        return agency;
    }
    @GetMapping("/stats") public ResponseEntity<?> stats(Authentication auth){
        Agency a=myAgency(auth); List<Trip> ts=trips.findByAgencyIdOrderByTravelDateAscDepartureTimeAsc(a.getAgencyId());
        List<Long> ids=ts.stream().map(Trip::getTripId).toList(); List<Booking> bs=ids.isEmpty()?List.of():bookings.findByTripIdIn(ids);
        long confirmed=bs.stream().filter(b->"CONFIRMED".equalsIgnoreCase(b.getStatus())).count();
        double gross=bs.stream().filter(b->"CONFIRMED".equalsIgnoreCase(b.getStatus())).mapToDouble(b->b.getTotalAmount()==null?0:b.getTotalAmount()).sum();
        int sold=bs.stream().filter(b->"CONFIRMED".equalsIgnoreCase(b.getStatus())).mapToInt(b->b.getSeatsBooked()==null?0:b.getSeatsBooked()).sum();
        return ResponseEntity.ok(Map.of("vehicles",vehicles.findByAgencyIdOrderByCreatedAtDesc(a.getAgencyId()).stream().filter(v->"ACTIVE".equalsIgnoreCase(v.getStatus())).count(),"departures",ts.size(),"activeDepartures",ts.stream().filter(t->"ACTIVE".equalsIgnoreCase(t.getStatus())).count(),"bookings",confirmed,"seatsSold",sold,"grossRevenue",gross));
    }
    @GetMapping("/vehicles") public ResponseEntity<?> myVehicles(Authentication auth){ return ResponseEntity.ok(vehicles.findByAgencyIdOrderByCreatedAtDesc(myAgency(auth).getAgencyId())); }
    @PostMapping("/vehicles") public ResponseEntity<?> addVehicle(@Valid @RequestBody CreateAgencyVehicleRequest r, Authentication auth){
        Agency a=myAgency(auth); String type=r.getVehicleType().trim(); String reg=r.getRegistrationNumber().trim().toUpperCase(); vehicleOptions.validate(type,r.getSeatingCapacity());
        if(vehicles.existsByRegistrationNumberIgnoreCase(reg)) throw new IllegalArgumentException("Vehicle registration number is already registered");
        AgencyVehicle v=new AgencyVehicle(); v.setAgencyId(a.getAgencyId()); v.setVehicleType(type); v.setRegistrationNumber(reg); v.setSeatingCapacity(r.getSeatingCapacity()); v.setStatus("ACTIVE");
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicles.save(v));
    }
    @DeleteMapping("/vehicles/{vehicleId}") public ResponseEntity<?> deactivateVehicle(@PathVariable Long vehicleId, Authentication auth){
        Agency a=myAgency(auth); AgencyVehicle v=vehicles.findById(vehicleId).orElseThrow(()->new IllegalArgumentException("Vehicle not found"));
        if(!v.getAgencyId().equals(a.getAgencyId())) throw new IllegalArgumentException("Vehicle does not belong to your agency");
        if(trips.findByAgencyVehicleId(vehicleId).stream().anyMatch(t->"ACTIVE".equalsIgnoreCase(t.getStatus())||"FULL".equalsIgnoreCase(t.getStatus()))) throw new IllegalArgumentException("Vehicle has an active departure. Cancel the departure before deactivating the vehicle.");
        v.setStatus("INACTIVE"); return ResponseEntity.ok(vehicles.save(v));
    }
    @GetMapping("/trips") public ResponseEntity<?> myTrips(Authentication auth){ return ResponseEntity.ok(trips.findByAgencyIdOrderByTravelDateAscDepartureTimeAsc(myAgency(auth).getAgencyId())); }
    @PostMapping("/trips") public ResponseEntity<?> createTrip(@Valid @RequestBody CreateAgencyTripRequest r, Authentication auth){
        Agency a=myAgency(auth); AgencyVehicle v=vehicles.findById(r.getAgencyVehicleId()).orElseThrow(()->new IllegalArgumentException("Vehicle not found"));
        if(!v.getAgencyId().equals(a.getAgencyId())) throw new IllegalArgumentException("Vehicle does not belong to your agency");
        if(!"ACTIVE".equalsIgnoreCase(v.getStatus())) throw new IllegalArgumentException("This vehicle is inactive");
        String mode=r.getBookingMode()==null?"SEAT_BASED":r.getBookingMode().trim().toUpperCase(); if(!mode.equals("SEAT_BASED")&&!mode.equals("FULL_VEHICLE")) throw new IllegalArgumentException("Booking mode must be SEAT_BASED or FULL_VEHICLE");
        int published=r.getPublishedSeats(); if(mode.equals("FULL_VEHICLE")) published=v.getSeatingCapacity();
        if(published>v.getSeatingCapacity()) throw new IllegalArgumentException("Published seats cannot exceed the verified vehicle capacity of "+v.getSeatingCapacity());
        Map<String,Object> fare=pricing.calculate(v.getVehicleType(),published,r.getDistanceKm(),Boolean.TRUE.equals(r.getReturnTrip()));
        Trip t=new Trip(); t.setFromLocation(r.getFromLocation().trim()); t.setToLocation(r.getToLocation().trim()); t.setTravelDate(r.getTravelDate()); t.setDepartureTime(r.getDepartureTime());
        t.setTotalSeats(published); t.setAvailableSeats(published); t.setPricePerSeat((Double)fare.get("pricePerPerson")); t.setVehicleType(v.getVehicleType()); t.setSeatingCapacity(v.getSeatingCapacity());
        t.setDistanceKm(r.getDistanceKm()); t.setReturnTrip(Boolean.TRUE.equals(r.getReturnTrip())); t.setBaseTripFare((Double)fare.get("baseTripFare")); t.setPlatformFee((Double)fare.get("platformFee")); t.setServiceFee((Double)fare.get("serviceFee")); t.setTotalTripFare((Double)fare.get("totalTripFare"));
        t.setDriverId(null); t.setAgencyId(a.getAgencyId()); t.setAgencyVehicleId(v.getVehicleId()); t.setBookingMode(mode); t.setPickupPoints(r.getPickupPoints()==null?null:r.getPickupPoints().trim()); t.setStatus("ACTIVE");
        return ResponseEntity.status(HttpStatus.CREATED).body(trips.save(t));
    }
    @GetMapping("/trips/{tripId}/bookings") public ResponseEntity<?> tripBookings(@PathVariable Long tripId, Authentication auth){ Trip t=ownedTrip(tripId,myAgency(auth)); return ResponseEntity.ok(bookings.findByTripId(t.getTripId())); }
    @DeleteMapping("/trips/{tripId}") @Transactional public ResponseEntity<?> cancelTrip(@PathVariable Long tripId, Authentication auth){
        Agency a=myAgency(auth); Trip t=ownedTrip(tripId,a); if("CANCELLED".equalsIgnoreCase(t.getStatus())) throw new IllegalArgumentException("Departure is already cancelled");
        t.setStatus("CANCELLED"); bookings.findByTripIdAndStatus(tripId,"CONFIRMED").forEach(b->{b.setStatus("CANCELLED");bookings.save(b);}); return ResponseEntity.ok(trips.save(t));
    }
    @DeleteMapping("/bookings/{bookingId}") @Transactional public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId, Authentication auth){
        Agency a=myAgency(auth); Booking b=bookings.findById(bookingId).orElseThrow(()->new IllegalArgumentException("Booking not found")); Trip t=ownedTrip(b.getTripId(),a);
        if(!"CONFIRMED".equalsIgnoreCase(b.getStatus())) throw new IllegalArgumentException("Only confirmed bookings can be cancelled");
        if("ACTIVE".equalsIgnoreCase(t.getStatus())||"FULL".equalsIgnoreCase(t.getStatus())){t.setAvailableSeats(t.getAvailableSeats()+b.getSeatsBooked());if("FULL".equalsIgnoreCase(t.getStatus()))t.setStatus("ACTIVE");trips.save(t);} b.setStatus("CANCELLED"); return ResponseEntity.ok(bookings.save(b));
    }
    private Trip ownedTrip(Long id,Agency a){ Trip t=trips.findById(id).orElseThrow(()->new IllegalArgumentException("Trip not found")); if(!a.getAgencyId().equals(t.getAgencyId())) throw new IllegalArgumentException("Departure does not belong to your agency"); return t; }
}
