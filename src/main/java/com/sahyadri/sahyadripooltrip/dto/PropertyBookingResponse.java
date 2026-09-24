package com.sahyadri.sahyadripooltrip.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PropertyBookingResponse {

    private Long bookingId;

    private Long propertyId;

    private String propertyName;

    private String propertyType;

    private String location;

    private Double pricePerNight;

    private Long travelerId;

    private Integer roomsBooked;

    private Double totalAmount;

    private String status;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private LocalDateTime bookedAt;
    private String paymentMethod; private String paymentStatus; private String couponCode; private Double discountAmount; private String checkInStatus; private String checkOutStatus; private String checkInTime; private String checkOutTime;

    public PropertyBookingResponse() {
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
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

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(Double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public Long getTravelerId() {
        return travelerId;
    }

    public void setTravelerId(Long travelerId) {
        this.travelerId = travelerId;
    }

    public Integer getRoomsBooked() {
        return roomsBooked;
    }

    public void setRoomsBooked(Integer roomsBooked) {
        this.roomsBooked = roomsBooked;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(LocalDateTime bookedAt) {
        this.bookedAt = bookedAt;
    }
    public String getPaymentMethod(){return paymentMethod;} public void setPaymentMethod(String v){paymentMethod=v;}
    public String getPaymentStatus(){return paymentStatus;} public void setPaymentStatus(String v){paymentStatus=v;}
    public String getCouponCode(){return couponCode;} public void setCouponCode(String v){couponCode=v;}
    public Double getDiscountAmount(){return discountAmount;} public void setDiscountAmount(Double v){discountAmount=v;}
    public String getCheckInStatus(){return checkInStatus;} public void setCheckInStatus(String v){checkInStatus=v;}
    public String getCheckOutStatus(){return checkOutStatus;} public void setCheckOutStatus(String v){checkOutStatus=v;}
    public String getCheckInTime(){return checkInTime;} public void setCheckInTime(String v){checkInTime=v;}
    public String getCheckOutTime(){return checkOutTime;} public void setCheckOutTime(String v){checkOutTime=v;}
}