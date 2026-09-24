package com.sahyadri.sahyadripooltrip.controller;
import com.sahyadri.sahyadripooltrip.entity.BikeRoadTrip; import com.sahyadri.sahyadripooltrip.repository.BikeRoadTripRepository; import com.sahyadri.sahyadripooltrip.service.BikeRoadTripCsvImportService; import org.springframework.http.ResponseEntity; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/bike-road-trips") public class BikeRoadTripController {
 private final BikeRoadTripRepository repo; private final BikeRoadTripCsvImportService importer;
 public BikeRoadTripController(BikeRoadTripRepository r,BikeRoadTripCsvImportService i){repo=r;importer=i;}
 @GetMapping public List<BikeRoadTrip> all(){return repo.findAll();}
 @GetMapping("/count") public long count(){return repo.count();}
 @PostMapping("/import") @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<?> importCsv(){try{return ResponseEntity.ok(Map.of("success",true,"routeCount",importer.importAll(),"message","Bike road trip master imported successfully."));}catch(Exception e){return ResponseEntity.internalServerError().body(Map.of("success",false,"message","Bike road trip import failed","error",String.valueOf(e.getMessage())));}}
}
