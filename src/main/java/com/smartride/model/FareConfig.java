package com.smartride.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "fare_config")
public class FareConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal baseFare;       // e.g. 30.00 rupees flat

    @Column(nullable = false)
    private BigDecimal ratePerKm;      // e.g. 12.00 rupees per km

    @Column(nullable = false)
    private boolean active;            // only one active config at a time

    // Constructors
    public FareConfig() {}

    public FareConfig(BigDecimal baseFare, BigDecimal ratePerKm, boolean active) {
        this.baseFare = baseFare;
        this.ratePerKm = ratePerKm;
        this.active = active;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public BigDecimal getBaseFare() { return baseFare; }
    public void setBaseFare(BigDecimal baseFare) { this.baseFare = baseFare; }
    public BigDecimal getRatePerKm() { return ratePerKm; }
    public void setRatePerKm(BigDecimal ratePerKm) { this.ratePerKm = ratePerKm; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}