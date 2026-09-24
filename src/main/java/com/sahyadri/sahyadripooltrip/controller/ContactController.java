package com.sahyadri.sahyadripooltrip.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.sahyadri.sahyadripooltrip.entity.ContactMessage;
import com.sahyadri.sahyadripooltrip.repository.ContactMessageRepository;
import com.sahyadri.sahyadripooltrip.service.NotificationService;

@RestController
@RequestMapping("/api/contact")
public class ContactController {
    private final ContactMessageRepository messages;
    private final NotificationService notifications;
    private final String adminEmail;

    public ContactController(ContactMessageRepository messages, NotificationService notifications,
            @Value("${app.mail.admin:${app.mail.from:}}") String adminEmail) {
        this.messages = messages;
        this.notifications = notifications;
        this.adminEmail = adminEmail;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> send(@RequestBody ContactRequest req) {
        required(req.name(), "Name");
        required(req.email(), "Email");
        required(req.subject(), "Subject");
        required(req.message(), "Message");
        if (!req.email().trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
            throw new IllegalArgumentException("Enter a valid email address");

        ContactMessage m = new ContactMessage();

        m.setName(req.name().trim());
        m.setEmail(req.email().trim());
        m.setPhone(req.phone() == null ? null : req.phone().trim());
        m.setSubject(req.subject().trim());
        m.setMessage(req.message().trim());
        m.setStatus("NEW");

        messages.save(m);
        // Database persistence is the primary submission path. Email is a secondary
        // notification and must never make a successfully saved contact message fail.
        if (adminEmail != null && !adminEmail.isBlank()) {
            try {
                notifications.sendContactMessage(adminEmail, m);
            } catch (Exception emailError) {
                // Keep the saved message; SMTP configuration/network problems should
                // not cause the website form to report a failed submission.
                System.err.println("Contact email notification failed: " + emailError.getMessage());
            }
        }
        return ResponseEntity.ok(Map.of("success", true, "message",
                "Message received successfully. Our support team has received your request."));
    }

    private static void required(String v, String n) {
        if (v == null || v.isBlank())
            throw new IllegalArgumentException(n + " is required");
    }

    public record ContactRequest(String name, String email, String phone, String subject, String message) {
    }
}
