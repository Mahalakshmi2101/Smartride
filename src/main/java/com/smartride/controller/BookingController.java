package com.smartride.controller;

import com.smartride.model.entity.Booking;
import com.smartride.model.entity.Ride;
import com.smartride.model.User;
import com.smartride.repository.BookingRepository;
import com.smartride.repository.RideRepository;
import com.smartride.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RideRepository rideRepository;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody Map<String, Object> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Long rideId = Long.valueOf(request.get("rideId").toString());
        Integer numberOfSeats = Integer.valueOf(request.get("numberOfSeats").toString());
        
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        
        if (ride.getAvailableSeats() < numberOfSeats) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Not enough seats available");
            return ResponseEntity.badRequest().body(response);
        }
        
        Booking booking = new Booking(user, ride, numberOfSeats);
        booking = bookingRepository.save(booking);
        
        ride.setAvailableSeats(ride.getAvailableSeats() - numberOfSeats);
        rideRepository.save(ride);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Booking created successfully");
        response.put("id", booking.getId());
        response.put("status", booking.getStatus());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Booking> bookings = bookingRepository.findByUser(user);
        
        List<Map<String, Object>> bookingList = bookings.stream()
            .map(booking -> {
                Map<String, Object> bookingMap = new HashMap<>();
                bookingMap.put("id", booking.getId());
                bookingMap.put("rideId", booking.getRide().getId());
                bookingMap.put("numberOfSeats", booking.getNumberOfSeats());
                bookingMap.put("status", booking.getStatus());
                bookingMap.put("bookingTime", booking.getBookingTime());
                return bookingMap;
            })
            .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("bookings", bookingList);
        response.put("count", bookingList.size());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBookingById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return bookingRepository.findByIdAndUser(id, user)
            .map(booking -> {
                Map<String, Object> response = new HashMap<>();
                response.put("id", booking.getId());
                response.put("rideId", booking.getRide().getId());
                response.put("numberOfSeats", booking.getNumberOfSeats());
                response.put("status", booking.getStatus());
                response.put("bookingTime", booking.getBookingTime());
                return ResponseEntity.ok(response);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBooking(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Booking booking = bookingRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (request.containsKey("status")) {
            booking.setStatus(request.get("status").toString());
        }
        if (request.containsKey("numberOfSeats")) {
            booking.setNumberOfSeats(Integer.valueOf(request.get("numberOfSeats").toString()));
        }
        
        booking = bookingRepository.save(booking);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Booking updated successfully");
        response.put("id", booking.getId());
        response.put("status", booking.getStatus());
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBooking(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Booking booking = bookingRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        bookingRepository.delete(booking);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Booking deleted successfully");
        
        return ResponseEntity.ok(response);
    }
}

