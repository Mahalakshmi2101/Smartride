package com.smartride.model;

import com.smartride.model.entity.Ride;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fares")
public class Fare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @Column(nullable = false)
    private double distanceKm;

    @Column(nullable = false)
    private BigDecimal totalFare;

    @Column(nullable = false)
    private BigDecimal perPassengerFare;

    @Column(nullable = false)
    private int passengerCount;

    @Column(nullable = false)
    private LocalDateTime calculatedAt;

    // Constructors
    public Fare() {}

    public Fare(Ride ride, double distanceKm, BigDecimal totalFare,
                BigDecimal perPassengerFare, int passengerCount) {
        this.ride = ride;
        this.distanceKm = distanceKm;
        this.totalFare = totalFare;
        this.perPassengerFare = perPassengerFare;
        this.passengerCount = passengerCount;
        this.calculatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public Ride getRide() { return ride; }
    public void setRide(Ride ride) { this.ride = ride; }
    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
    public BigDecimal getTotalFare() { return totalFare; }
    public void setTotalFare(BigDecimal totalFare) { this.totalFare = totalFare; }
    public BigDecimal getPerPassengerFare() { return perPassengerFare; }
    public void setPerPassengerFare(BigDecimal perPassengerFare) { this.perPassengerFare = perPassengerFare; }
    public int getPassengerCount() { return passengerCount; }
    public void setPassengerCount(int passengerCount) { this.passengerCount = passengerCount; }
    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }
}