package com.smartride.controller;

import com.smartride.model.User;
import com.smartride.model.entity.Ride;
import com.smartride.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    @Autowired
    private RideRepository rideRepository;

    // POST a new ride
    @PostMapping
    public ResponseEntity<?> postRide(
            @RequestBody Ride rideRequest,
            Authentication authentication) {
        try {
            User driver = (User) authentication.getPrincipal();
            rideRequest.setDriver(driver);
            Ride saved = rideRepository.save(rideRequest);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // GET all rides for the logged-in driver
    @GetMapping("/my-rides")
    public ResponseEntity<?> getMyRides(Authentication authentication) {
        try {
            User driver = (User) authentication.getPrincipal();
            List<Ride> rides = rideRepository.findByDriver(driver);
            return ResponseEntity.ok(rides);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // GET all active rides (for passenger search)
    @GetMapping("/search")
    public ResponseEntity<?> searchRides(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String date) {
        try {
            List<Ride> all = rideRepository.findByStatus(Ride.RideStatus.ACTIVE);

            // filter by source
            if (source != null && !source.isEmpty()) {
                all = all.stream()
                        .filter(r -> r.getSource()
                                .toLowerCase()
                                .contains(source.toLowerCase()))
                        .collect(java.util.stream.Collectors.toList());
            }

            // filter by destination
            if (destination != null && !destination.isEmpty()) {
                all = all.stream()
                        .filter(r -> r.getDestination()
                                .toLowerCase()
                                .contains(destination.toLowerCase()))
                        .collect(java.util.stream.Collectors.toList());
            }

            // filter by date
            if (date != null && !date.isEmpty()) {
                all = all.stream()
                        .filter(r -> r.getRideDate().toString().equals(date))
                        .collect(java.util.stream.Collectors.toList());
            }

            return ResponseEntity.ok(all);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // GET single ride by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getRideById(@PathVariable Long id) {
        return rideRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}