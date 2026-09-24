package com.sahyadri.sahyadripooltrip.dto;

public class PasswordResetConfirmRequest {
    private String resetToken;
    private String newPassword;

    public PasswordResetConfirmRequest() {
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String v) {
        resetToken = v;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String v) {
        newPassword = v;
    }
}
