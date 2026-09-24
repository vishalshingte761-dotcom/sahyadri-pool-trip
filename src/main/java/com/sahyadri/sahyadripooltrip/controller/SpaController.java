package com.sahyadri.sahyadripooltrip.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Keeps direct browser navigation/bookmarks working for the hash-router SPA.
 * The frontend still renders routes from index.html; this controller only
 * forwards clean URLs to the same entry page.
 */
@Controller
public class SpaController {
    @GetMapping({
        "/trips", "/find-trips", "/driver-register", "/agency-register", "/hotel-register",
        "/register", "/login", "/forts", "/spots", "/stays", "/about", "/contact",
        "/partner", "/safety", "/complaint", "/profile", "/dashboard", "/create-trip",
        "/create-property", "/return", "/how", "/privacy", "/terms", "/refund", "/trust",
        "/forgot-password", "/settings-password", "/india-nature", "/nature"
    })
    public String spaRoute() {
        return "forward:/index.html";
    }
}
