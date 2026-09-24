package com.sahyadri.sahyadripooltrip.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class DriverEmergencyContactRequest {

    @Size(max = 100, message = "Emergency contact name is too long")
    private String emergencyContactName;

    @Pattern(
        regexp = "^$|^[6-9]\\d{9}$",
        message = "Invalid emergency contact number"
    )
    private String emergencyContactNumber;

    @Size(max = 50, message = "Emergency contact relation is too long")
    private String emergencyContactRelation;

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactNumber() {
        return emergencyContactNumber;
    }

    public void setEmergencyContactNumber(String emergencyContactNumber) {
        this.emergencyContactNumber = emergencyContactNumber;
    }

    public String getEmergencyContactRelation() {
        return emergencyContactRelation;
    }

    public void setEmergencyContactRelation(String emergencyContactRelation) {
        this.emergencyContactRelation = emergencyContactRelation;
    }
}