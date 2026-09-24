package com.sahyadri.sahyadripooltrip.dto;

import java.time.LocalDate;

public class PropertyAvailabilityResponse {

    private Long propertyId;
    private String propertyName;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private Integer totalRooms;
    private Integer bookedRooms;
    private Integer availableRooms;

    public PropertyAvailabilityResponse() {
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Integer getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }

    public Integer getBookedRooms() {
        return bookedRooms;
    }

    public void setBookedRooms(Integer bookedRooms) {
        this.bookedRooms = bookedRooms;
    }

    public Integer getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(Integer availableRooms) {
        this.availableRooms = availableRooms;
    }
}