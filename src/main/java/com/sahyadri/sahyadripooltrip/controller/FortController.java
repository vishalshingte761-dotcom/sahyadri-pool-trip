package com.sahyadri.sahyadripooltrip.controller;

import com.sahyadri.sahyadripooltrip.entity.Fort;
import com.sahyadri.sahyadripooltrip.entity.FortRoute;
import com.sahyadri.sahyadripooltrip.service.FortService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forts")
@CrossOrigin(origins = "*")
public class FortController {

        private final FortService fortService;

        public FortController(FortService fortService) {
                this.fortService = fortService;
        }

        @GetMapping
        public ResponseEntity<List<Fort>> getAllForts() {
                return ResponseEntity.ok(fortService.getAllForts());
        }

        @GetMapping("/{fortId}")
        public ResponseEntity<Fort> getFortById(
                        @PathVariable String fortId) {
                return fortService.getFortById(fortId)
                                .map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @GetMapping("/name/{fortName}")
        public ResponseEntity<Fort> getFortByName(
                        @PathVariable String fortName) {
                return fortService.getFortByName(fortName)
                                .map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @GetMapping("/district/{district}")
        public ResponseEntity<List<Fort>> getFortsByDistrict(
                        @PathVariable String district) {
                return ResponseEntity.ok(
                                fortService.getFortsByDistrict(district));
        }

        @GetMapping("/status/{recordStatus}")
        public ResponseEntity<List<Fort>> getFortsByRecordStatus(
                        @PathVariable String recordStatus) {
                return ResponseEntity.ok(
                                fortService.getFortsByRecordStatus(recordStatus));
        }

        @GetMapping("/verification/{verificationStatus}")
        public ResponseEntity<List<Fort>> getFortsByVerificationStatus(
                        @PathVariable String verificationStatus) {
                return ResponseEntity.ok(
                                fortService.getFortsByVerificationStatus(
                                                verificationStatus));
        }

        @GetMapping("/{fortId}/routes")
        public ResponseEntity<List<FortRoute>> getRoutesByFortId(
                        @PathVariable String fortId) {
                if (!fortService.fortExists(fortId)) {
                        return ResponseEntity.notFound().build();
                }

                return ResponseEntity.ok(
                                fortService.getRoutesByFortId(fortId));
        }

        @GetMapping("/routes/{routeId}")
        public ResponseEntity<FortRoute> getRouteById(@PathVariable String routeId) {
                return fortService.getRouteById(routeId)
                                .map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @GetMapping("/routes/difficulty")
        public ResponseEntity<List<FortRoute>> getRoutesByDifficulty(
                        @RequestParam String value) {
                return ResponseEntity.ok(
                                fortService.getRoutesByDifficulty(value));
        }

        @GetMapping("/count")
        public ResponseEntity<Long> getFortCount() {
                return ResponseEntity.ok(fortService.getFortCount());
        }

        @GetMapping("/route-count")
        public ResponseEntity<Long> getRouteCount() {
                return ResponseEntity.ok(fortService.getRouteCount());
        }
}