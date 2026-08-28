package com.smartride.dto;

public class AdminStatsResponse {
    private long totalUsers;
    private long totalRides;
    private long totalBookings;
    private long totalPayments;
    private double totalEarnings;
    private long activeRides;
    private long cancelledRides;

    public AdminStatsResponse() {}

    public AdminStatsResponse(long totalUsers, long totalRides, long totalBookings,
                               long totalPayments, double totalEarnings,
                               long activeRides, long cancelledRides) {
        this.totalUsers = totalUsers;
        this.totalRides = totalRides;
        this.totalBookings = totalBookings;
        this.totalPayments = totalPayments;
        this.totalEarnings = totalEarnings;
        this.activeRides = activeRides;
        this.cancelledRides = cancelledRides;
    }

    public long getTotalUsers() { return totalUsers; }
    public long getTotalRides() { return totalRides; }
    public long getTotalBookings() { return totalBookings; }
    public long getTotalPayments() { return totalPayments; }
    public double getTotalEarnings() { return totalEarnings; }
    public long getActiveRides() { return activeRides; }
    public long getCancelledRides() { return cancelledRides; }
}