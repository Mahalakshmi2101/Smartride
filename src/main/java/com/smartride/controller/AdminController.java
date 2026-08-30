package com.smartride.controller;

import com.smartride.repository.BookingRepository;
import com.smartride.repository.RideRepository;
import com.smartride.repository.UserRepository;
import com.smartride.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final BookingRepository bookingRepository;
    private final AdminService adminService;

    public AdminController(UserRepository userRepository,
                           RideRepository rideRepository,
                           BookingRepository bookingRepository,
                           AdminService adminService) {
        this.userRepository    = userRepository;
        this.rideRepository    = rideRepository;
        this.bookingRepository = bookingRepository;
        this.adminService      = adminService;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(Map.of(
            "totalUsers",    userRepository.count(),
            "totalRides",    rideRepository.count(),
            "totalBookings", bookingRepository.count()
        ));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/rides")
    public ResponseEntity<?> getAllRides() {
        return ResponseEntity.ok(rideRepository.findAll());
    }

    @GetMapping("/payments")
    public ResponseEntity<List<?>> getAllPayments() {
        return ResponseEntity.ok(adminService.getAllPayments());
    }

    @DeleteMapping("/users/{userId}/block")
    public ResponseEntity<String> blockUser(@PathVariable Long userId) {
        adminService.blockUser(userId);
        return ResponseEntity.ok("User blocked successfully");
    }
}