package com.sahyadri.sahyadripooltrip.entity;
import jakarta.persistence.*;
@Entity @Table(name="bike_road_trips")
public class BikeRoadTrip {
 @Id @Column(length=30) private String routeId;
 @Column(nullable=false,length=180) private String routeName;
 @Column(length=120) private String startPoint;
 @Column(length=120) private String endPoint;
 @Column(length=180) private String states;
 private Double routeDistanceKm;
 @Column(length=40) private String difficulty;
 @Column(length=100) private String bestSeason;
 @Column(length=1000) private String highlights;
 @Column(length=500) private String photoQuery;
 @Column(length=500) private String primarySource;
 @Column(length=80) private String verificationStatus;
 @Column(length=40) private String recordStatus;
 public String getRouteId(){return routeId;} public void setRouteId(String v){routeId=v;}
 public String getRouteName(){return routeName;} public void setRouteName(String v){routeName=v;}
 public String getStartPoint(){return startPoint;} public void setStartPoint(String v){startPoint=v;}
 public String getEndPoint(){return endPoint;} public void setEndPoint(String v){endPoint=v;}
 public String getStates(){return states;} public void setStates(String v){states=v;}
 public Double getRouteDistanceKm(){return routeDistanceKm;} public void setRouteDistanceKm(Double v){routeDistanceKm=v;}
 public String getDifficulty(){return difficulty;} public void setDifficulty(String v){difficulty=v;}
 public String getBestSeason(){return bestSeason;} public void setBestSeason(String v){bestSeason=v;}
 public String getHighlights(){return highlights;} public void setHighlights(String v){highlights=v;}
 public String getPhotoQuery(){return photoQuery;} public void setPhotoQuery(String v){photoQuery=v;}
 public String getPrimarySource(){return primarySource;} public void setPrimarySource(String v){primarySource=v;}
 public String getVerificationStatus(){return verificationStatus;} public void setVerificationStatus(String v){verificationStatus=v;}
 public String getRecordStatus(){return recordStatus;} public void setRecordStatus(String v){recordStatus=v;}
}
