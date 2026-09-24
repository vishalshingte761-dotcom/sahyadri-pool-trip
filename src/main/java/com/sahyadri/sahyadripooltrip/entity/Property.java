package com.sahyadri.sahyadripooltrip.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name="properties")
public class Property {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long propertyId;
 @Column(nullable=false) private Long ownerId;
 @Column(nullable=false) private String propertyName;
 @Column(nullable=false) private String propertyType;
 @Column(nullable=false) private String location;
 @Column(length=1000) private String description;
 @Column(nullable=false) private Integer totalRooms;
 @Column(nullable=false) private Integer availableRooms;
 @Column(nullable=false) private Double pricePerNight;
 @Column(nullable=false) private String status;
 @Column(length=2000) private String photoUrl;
 private String checkInTime="12:00 PM";
 private String checkOutTime="10:00 AM";
 private String paymentMode="BOTH";
 @Column(length=80) private String couponCode;
 private Double couponDiscount=0.0;
 private Double couponMinAmount=0.0;
 @Column(nullable=false,updatable=false) private LocalDateTime createdAt;
 @PrePersist protected void onCreate(){createdAt=LocalDateTime.now(); if(status==null) status="ACTIVE"; if(availableRooms==null) availableRooms=totalRooms;}
 public Property(){}
 public Long getPropertyId(){return propertyId;} public void setPropertyId(Long v){propertyId=v;}
 public Long getOwnerId(){return ownerId;} public void setOwnerId(Long v){ownerId=v;}
 public String getPropertyName(){return propertyName;} public void setPropertyName(String v){propertyName=v;}
 public String getPropertyType(){return propertyType;} public void setPropertyType(String v){propertyType=v;}
 public String getLocation(){return location;} public void setLocation(String v){location=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public Integer getTotalRooms(){return totalRooms;} public void setTotalRooms(Integer v){totalRooms=v;}
 public Integer getAvailableRooms(){return availableRooms;} public void setAvailableRooms(Integer v){availableRooms=v;}
 public Double getPricePerNight(){return pricePerNight;} public void setPricePerNight(Double v){pricePerNight=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;}
 public String getPhotoUrl(){return photoUrl;} public void setPhotoUrl(String v){photoUrl=v;}
 public String getCheckInTime(){return checkInTime;} public void setCheckInTime(String v){checkInTime=v;}
 public String getCheckOutTime(){return checkOutTime;} public void setCheckOutTime(String v){checkOutTime=v;}
 public String getPaymentMode(){return paymentMode;} public void setPaymentMode(String v){paymentMode=v;}
 public String getCouponCode(){return couponCode;} public void setCouponCode(String v){couponCode=v;}
 public Double getCouponDiscount(){return couponDiscount;} public void setCouponDiscount(Double v){couponDiscount=v;}
 public Double getCouponMinAmount(){return couponMinAmount;} public void setCouponMinAmount(Double v){couponMinAmount=v;}
 public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
}
