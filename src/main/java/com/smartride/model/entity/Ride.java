package com.smartride.model.entity;
import com.smartride.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;  // ✅ ONLY this, @Future removed
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

// This is the "Notice Board Post" — every ride a driver offers
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id", nullable = false)
    @JsonIgnoreProperties({"password", "authorities", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled", "hibernateLazyInitializer", "handler"})
    private User driver;
   
    @Column(nullable = false)
    @NotBlank(message = "Source is required")
    private String source;

    @Column(nullable = false)
    @NotBlank(message = "Destination is required")
    private String destination;

    @Column(name = "ride_date", nullable = false)
    @FutureOrPresent(message = "Ride date must be today or in the future")
    private LocalDate rideDate;

    @Column(name = "ride_time", nullable = false)
    private LocalTime rideTime;

    @Column(name = "available_seats", nullable = false)
    @Min(value = 0, message = "Seats cannot be negative")  // ✅ CHANGED 1 → 0
    private Integer availableSeats;

    @Column(name = "price_per_seat", nullable = false)
    @Min(value = 0, message = "Price cannot be negative")
    private Double pricePerSeat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status = RideStatus.ACTIVE;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = RideStatus.ACTIVE;
        }
    }

    public enum RideStatus {
        ACTIVE,
        COMPLETED,
        CANCELLED
    }
}