package com.smartride.controller;

import com.smartride.model.entity.Booking;
import com.smartride.model.entity.Ride;
import com.smartride.model.User;
import com.smartride.repository.BookingRepository;
import com.smartride.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RideRepository rideRepository;

    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();

            Long rideId = Long.valueOf(request.get("rideId").toString());
            Integer seats = Integer.valueOf(
                request.getOrDefault("seatsBooked",
                request.getOrDefault("numberOfSeats", "1")).toString()
            );

            Ride ride = rideRepository.findById(rideId)
                    .orElseThrow(() -> new RuntimeException("Ride not found"));

            if (ride.getAvailableSeats() < seats) {
                Map<String, Object> err = new HashMap<>();
                err.put("message", "Not enough seats available");
                return ResponseEntity.badRequest().body(err);
            }

            Booking booking = new Booking(user, ride, seats);
            booking.setStatus("CONFIRMED");
            booking = bookingRepository.save(booking);

            ride.setAvailableSeats(ride.getAvailableSeats() - seats);
            rideRepository.save(ride);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Booking confirmed");
            response.put("id", booking.getId());
            response.put("status", booking.getStatus());
            response.put("totalAmount", seats * ride.getPricePerSeat());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("message", e.getMessage());
            return ResponseEntity.status(500).body(err);
        }
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<?> getMyBookings(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<Booking> bookings = bookingRepository.findByUser(user);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("message", e.getMessage());
            return ResponseEntity.status(500).body(err);
        }
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<?> getBookingsByRide(
            @PathVariable Long rideId,
            Authentication authentication) {
        try {
            Ride ride = rideRepository.findById(rideId)
                    .orElseThrow(() -> new RuntimeException("Ride not found"));
            List<Booking> bookings = bookingRepository.findByRide(ride);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("message", e.getMessage());
            return ResponseEntity.status(500).body(err);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return bookingRepository.findByIdAndUser(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Booking booking = bookingRepository.findByIdAndUser(id, user)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            if ("CANCELLED".equals(booking.getStatus())) {
                Map<String, Object> err = new HashMap<>();
                err.put("message", "Booking already cancelled");
                return ResponseEntity.badRequest().body(err);
            }

            // Restore seats to the ride
            Ride ride = booking.getRide();
            ride.setAvailableSeats(ride.getAvailableSeats() + booking.getNumberOfSeats());
            rideRepository.save(ride);

            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Booking cancelled");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("message", e.getMessage());
            return ResponseEntity.status(500).body(err);
        }
    }
}