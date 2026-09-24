package com.sahyadri.sahyadripooltrip.dto;

public class AdminStatsResponse {

    private long totalUsers;
    private long totalTravelers;
    private long totalDrivers;
    private long totalHotelOwners;

    private long totalTrips;
    private long activeTrips;
    private long cancelledTrips;

    private long totalBookings;
    private long confirmedBookings;
    private long cancelledBookings;

    private long totalProperties;
    private long activeProperties;
    private long inactiveProperties;

    private long totalPropertyBookings;
    private long confirmedPropertyBookings;
    private long cancelledPropertyBookings;

    public AdminStatsResponse(
            long totalUsers,
            long totalTravelers,
            long totalDrivers,
            long totalHotelOwners,
            long totalTrips,
            long activeTrips,
            long cancelledTrips,
            long totalBookings,
            long confirmedBookings,
            long cancelledBookings,
            long totalProperties,
            long activeProperties,
            long inactiveProperties,
            long totalPropertyBookings,
            long confirmedPropertyBookings,
            long cancelledPropertyBookings) {

        this.totalUsers = totalUsers;
        this.totalTravelers = totalTravelers;
        this.totalDrivers = totalDrivers;
        this.totalHotelOwners = totalHotelOwners;

        this.totalTrips = totalTrips;
        this.activeTrips = activeTrips;
        this.cancelledTrips = cancelledTrips;

        this.totalBookings = totalBookings;
        this.confirmedBookings = confirmedBookings;
        this.cancelledBookings = cancelledBookings;

        this.totalProperties = totalProperties;
        this.activeProperties = activeProperties;
        this.inactiveProperties = inactiveProperties;

        this.totalPropertyBookings = totalPropertyBookings;
        this.confirmedPropertyBookings = confirmedPropertyBookings;
        this.cancelledPropertyBookings = cancelledPropertyBookings;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getTotalTravelers() {
        return totalTravelers;
    }

    public long getTotalDrivers() {
        return totalDrivers;
    }

    public long getTotalHotelOwners() {
        return totalHotelOwners;
    }

    public long getTotalTrips() {
        return totalTrips;
    }

    public long getActiveTrips() {
        return activeTrips;
    }

    public long getCancelledTrips() {
        return cancelledTrips;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public long getConfirmedBookings() {
        return confirmedBookings;
    }

    public long getCancelledBookings() {
        return cancelledBookings;
    }

    public long getTotalProperties() {
        return totalProperties;
    }

    public long getActiveProperties() {
        return activeProperties;
    }

    public long getInactiveProperties() {
        return inactiveProperties;
    }

    public long getTotalPropertyBookings() {
        return totalPropertyBookings;
    }

    public long getConfirmedPropertyBookings() {
        return confirmedPropertyBookings;
    }

    public long getCancelledPropertyBookings() {
        return cancelledPropertyBookings;
    }
}