package com.sahyadri.sahyadripooltrip.controller;

import com.sahyadri.sahyadripooltrip.service.FortCsvImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/forts/import")
public class FortImportController {

    private final FortCsvImportService fortCsvImportService;

    public FortImportController(
            FortCsvImportService fortCsvImportService
    ) {
        this.fortCsvImportService = fortCsvImportService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> importForts() {

        try {

            FortCsvImportService.ImportResult result =
                    fortCsvImportService.importForts();

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message",
                            "Maharashtra forts master CSV imported successfully.",
                            "fortCount",
                            result.fortCount(),
                            "routeCount",
                            result.routeCount(),
                            "skippedRouteCount",
                            result.skippedRouteCount()
                    )
            );

        } catch (Exception exception) {

            return ResponseEntity.internalServerError().body(
                    Map.of(
                            "success", false,
                            "message",
                            "Fort CSV import failed.",
                            "error",
                            exception.getMessage()
                    )
            );
        }
    }
}