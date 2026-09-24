package com.sahyadri.sahyadripooltrip.dto;

public class PasswordResetVerifyRequest {
    private String identifier;
    private String otp;

    public PasswordResetVerifyRequest() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String v) {
        identifier = v;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String v) {
        otp = v;
    }
}
