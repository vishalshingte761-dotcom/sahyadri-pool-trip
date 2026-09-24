package com.sahyadri.sahyadripooltrip.controller;

import com.sahyadri.sahyadripooltrip.entity.Spot;
import com.sahyadri.sahyadripooltrip.service.SpotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/spots")
@CrossOrigin(origins = "*")
public class SpotController {
    private final SpotService service;
    public SpotController(SpotService service){this.service=service;}
    @GetMapping public ResponseEntity<List<Spot>> getAll(){return ResponseEntity.ok(service.getAllSpots());}
    @GetMapping("/count") public ResponseEntity<Long> count(){return ResponseEntity.ok(service.count());}
    @GetMapping("/name/{name}") public ResponseEntity<Spot> byName(@PathVariable String name){return service.getSpotByName(name).map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());}
    @GetMapping("/category/{category}") public ResponseEntity<List<Spot>> byCategory(@PathVariable String category){return ResponseEntity.ok(service.getByCategory(category));}
    @GetMapping("/district/{district}") public ResponseEntity<List<Spot>> byDistrict(@PathVariable String district){return ResponseEntity.ok(service.getByDistrict(district));}
    @GetMapping("/status/{status}") public ResponseEntity<List<Spot>> byStatus(@PathVariable String status){return ResponseEntity.ok(service.getByRecordStatus(status));}
    @GetMapping("/verification/{status}") public ResponseEntity<List<Spot>> byVerification(@PathVariable String status){return ResponseEntity.ok(service.getByVerificationStatus(status));}
    @GetMapping("/{spotId}") public ResponseEntity<Spot> byId(@PathVariable String spotId){return service.getSpotById(spotId).map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());}
}
