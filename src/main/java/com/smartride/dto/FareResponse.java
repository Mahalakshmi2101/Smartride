package com.smartride.dto;

public class FareResponse {
    private double distanceKm;
    private java.math.BigDecimal totalFare;
    private java.math.BigDecimal perPassengerFare;
    private int passengerCount;

    public FareResponse(double distanceKm, java.math.BigDecimal totalFare,
                        java.math.BigDecimal perPassengerFare, int passengerCount) {
        this.distanceKm = distanceKm;
        this.totalFare = totalFare;
        this.perPassengerFare = perPassengerFare;
        this.passengerCount = passengerCount;
    }

    public double getDistanceKm() { return distanceKm; }
    public java.math.BigDecimal getTotalFare() { return totalFare; }
    public java.math.BigDecimal getPerPassengerFare() { return perPassengerFare; }
    public int getPassengerCount() { return passengerCount; }
}