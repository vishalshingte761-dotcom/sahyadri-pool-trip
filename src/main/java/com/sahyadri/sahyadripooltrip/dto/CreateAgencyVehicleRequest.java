package com.sahyadri.sahyadripooltrip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class CreateAgencyVehicleRequest {
    @NotBlank private String vehicleType;
    @NotBlank private String registrationNumber;
    @NotNull @Min(1) private Integer seatingCapacity;

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String v) { vehicleType = v; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String v) { registrationNumber = v; }
    public Integer getSeatingCapacity() { return seatingCapacity; }
    public void setSeatingCapacity(Integer v) { seatingCapacity = v; }
}
