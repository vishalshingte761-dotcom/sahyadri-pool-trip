package com.sahyadri.sahyadripooltrip.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sahyadri.sahyadripooltrip.entity.Complaint;
import com.sahyadri.sahyadripooltrip.entity.ComplaintAttachment;
import com.sahyadri.sahyadripooltrip.entity.ComplaintPriority;
import com.sahyadri.sahyadripooltrip.entity.ComplaintStatus;
import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.repository.ComplaintAttachmentRepository;
import com.sahyadri.sahyadripooltrip.repository.ComplaintRepository;
import com.sahyadri.sahyadripooltrip.repository.UserRepository;
import com.sahyadri.sahyadripooltrip.service.FileStorageService;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {
    private final UserRepository users;
    private final ComplaintRepository complaints;
    private final ComplaintAttachmentRepository attachments;
    private final FileStorageService files;

    public ComplaintController(UserRepository u, ComplaintRepository c, ComplaintAttachmentRepository a,
            FileStorageService f) {
        users = u;
        complaints = c;
        attachments = a;
        files = f;
    }

    private User me(Authentication a) {
        return users.findByEmail(a.getName()).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(@RequestParam String category, @RequestParam String subject,
            @RequestParam String description, @RequestParam(required = false) Long relatedTripId,
            @RequestParam(required = false) Long relatedBookingId,
            @RequestParam(required = false) Long relatedPropertyId,
            @RequestParam(required = false, defaultValue = "NORMAL") ComplaintPriority priority,
            @RequestPart(required = false) MultipartFile[] filesIn, Authentication auth) {
        if (category == null || category.isBlank())
            throw new IllegalArgumentException("Complaint category is required");
        if (subject == null || subject.isBlank())
            throw new IllegalArgumentException("Complaint subject is required");
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Complaint description is required");
        if (filesIn != null && filesIn.length > 10)
            throw new IllegalArgumentException("Maximum 10 complaint attachments are allowed");

        Complaint c = new Complaint();
        c.setUserId(me(auth).getUserId());
        c.setCategory(category.trim());
        c.setSubject(subject.trim());
        c.setDescription(description.trim());
        c.setRelatedTripId(relatedTripId);
        c.setRelatedBookingId(relatedBookingId);
        c.setRelatedPropertyId(relatedPropertyId);
        c.setPriority(priority);
        c = complaints.save(c);
        if (filesIn != null) {
            for (MultipartFile f : filesIn) {
                if (f == null || f.isEmpty())
                    continue;

                String type = f.getContentType() == null
                        ? ""
                        : f.getContentType().toLowerCase();

                String fileName = f.getOriginalFilename() == null
                        ? ""
                        : f.getOriginalFilename().toLowerCase();

                boolean image = type.startsWith("image/")
                        || fileName.endsWith(".jpg")
                        || fileName.endsWith(".jpeg")
                        || fileName.endsWith(".png")
                        || fileName.endsWith(".webp");

                boolean video = type.startsWith("video/")
                        || fileName.endsWith(".mp4")
                        || fileName.endsWith(".mov")
                        || fileName.endsWith(".avi")
                        || fileName.endsWith(".webm");

                if (!image && !video)
                    throw new IllegalArgumentException("Complaint attachments must be images or videos");

                long max = video
                        ? 50L * 1024 * 1024
                        : 10L * 1024 * 1024;
                String path = files.store(f, "complaints", max, true);
                ComplaintAttachment x = new ComplaintAttachment();
                x.setComplaintId(c.getComplaintId());
                x.setOriginalFileName(f.getOriginalFilename() == null ? "attachment" : f.getOriginalFilename());
                x.setStoredFileName(path);
                x.setContentType(type);
                x.setFileSize(f.getSize());
                attachments.save(x);
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(c);
    }

    @GetMapping("/my")
    public List<Complaint> my(Authentication a) {
        return complaints.findByUserIdOrderByCreatedAtDesc(me(a).getUserId());
    }

    @GetMapping("/{id}/attachments")
    public List<ComplaintAttachment> attachmentList(@PathVariable Long id, Authentication a) {
        Complaint c = complaints.findById(id).orElseThrow(() -> new IllegalArgumentException("Complaint not found"));
        if (!c.getUserId().equals(me(a).getUserId()))
            throw new IllegalArgumentException("Not your complaint");
        return attachments.findByComplaintId(id);
    }

    @GetMapping("/admin")
    public List<Complaint> admin() {
        return complaints.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/admin/{id}/attachments")
    public List<ComplaintAttachment> adminAttachments(@PathVariable Long id) {
        return attachments.findByComplaintId(id);
    }

    @PutMapping("/admin/{id}")
    public Complaint update(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication a) {
        Complaint c = complaints.findById(id).orElseThrow(() -> new IllegalArgumentException("Complaint not found"));
        String status = body.get("status");
        if (status != null)
            c.setStatus(ComplaintStatus.valueOf(status));
        c.setAdminResponse(body.get("adminResponse"));
        c.setHandledBy(me(a).getUserId());
        c.setHandledAt(java.time.LocalDateTime.now());
        return complaints.save(c);
    }
}
