package com.smartride.model.entity;

import com.smartride.model.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;
    
    @Column(nullable = false)
    private Integer numberOfSeats;
    
    @Column(nullable = false)
    private LocalDateTime bookingTime;
    
    @Column(nullable = false)
    private String status = "PENDING";
    
    public Booking() {
    }
    
    public Booking(User user, Ride ride, Integer numberOfSeats) {
        this.user = user;
        this.ride = ride;
        this.numberOfSeats = numberOfSeats;
        this.bookingTime = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Ride getRide() {
        return ride;
    }
    
    public void setRide(Ride ride) {
        this.ride = ride;
    }
    
    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }
    
    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }
    
    public LocalDateTime getBookingTime() {
        return bookingTime;
    }
    
    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}
