package com.sahyadri.sahyadripooltrip.controller;

import java.io.IOException;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.sahyadri.sahyadripooltrip.service.FileStorageService;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final FileStorageService files;

    public DocumentController(FileStorageService f) {
        files = f;
    }

    @GetMapping
    public ResponseEntity<?> read(
            @RequestParam(required = false) String path,
            Authentication auth) throws IOException {

        if (auth.getAuthorities().stream()
                .noneMatch(x -> x.getAuthority().equals("ROLE_ADMIN"))) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    java.util.Map.of(
                            "success", false,
                            "message", "Only admin can access verification documents"));
        }

        if (path == null || path.isBlank()) {
            return ResponseEntity.badRequest().body(
                    java.util.Map.of(
                            "success", false,
                            "message", "Document path is required"));
        }

        byte[] data = files.read(path);

        String lowerPath = path.toLowerCase();

        String ct = lowerPath.endsWith(".pdf")
                ? "application/pdf"
                : "application/octet-stream";

        if (lowerPath.matches(".*\\.(jpg|jpeg|png|webp)$")) {
            if (lowerPath.endsWith(".png")) {
                ct = "image/png";
            } else if (lowerPath.endsWith(".webp")) {
                ct = "image/webp";
            } else {
                ct = "image/jpeg";
            }
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(ct))
                .body(data);
    }
}
