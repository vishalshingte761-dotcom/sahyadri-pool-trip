package com.sahyadri.sahyadripooltrip.controller;

import com.sahyadri.sahyadripooltrip.service.SpotCsvImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/spots/import")
public class SpotImportController {
    private final SpotCsvImportService service;
    public SpotImportController(SpotCsvImportService service){this.service=service;}
    @PostMapping
    public ResponseEntity<Map<String,Object>> importSpots(){
        try { var result=service.importSpots(); return ResponseEntity.ok(Map.of("success",true,"message","Maharashtra spots master CSV imported successfully.","spotCount",result.spotCount())); }
        catch(Exception e){ return ResponseEntity.internalServerError().body(Map.of("success",false,"message","Spots CSV import failed.","error",String.valueOf(e.getMessage()))); }
    }
}
