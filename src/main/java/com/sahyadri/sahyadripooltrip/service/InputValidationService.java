package com.sahyadri.sahyadripooltrip.service;

import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class InputValidationService {
    private static final Pattern EMAIL = Pattern.compile("^[a-z0-9][a-z0-9._%+-]*@[a-z0-9.-]+\\.[a-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^[6-9]\\d{9}$");

    public String email(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Email is required");
        String v = value.trim();
        if (!v.equals(v.toLowerCase()))
            throw new IllegalArgumentException("Email must contain lowercase letters only");
        if (!EMAIL.matcher(v).matches())
            throw new IllegalArgumentException("Enter a valid email address");
        return v;
    }

    public String phone(String value, boolean required) {
        if (value == null || value.isBlank()) {
            if (required)
                throw new IllegalArgumentException("Phone number is required");
            return null;
        }
        String v = value.trim();
        if (!PHONE.matcher(v).matches())
            throw new IllegalArgumentException("Phone number must be exactly 10 digits and start with 6-9");
        return v;
    }
}
