package com.smartride.service;

import com.smartride.dto.AdminStatsResponse;
import com.smartride.model.entity.Ride;
import com.smartride.model.Payment;
import com.smartride.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired private UserRepository userRepository;
    @Autowired private RideRepository rideRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private PaymentRepository paymentRepository;

    public AdminStatsResponse getStats() {
        long totalUsers     = userRepository.count();
        long totalRides     = rideRepository.count();
        long totalBookings  = bookingRepository.count();
        long totalPayments  = paymentRepository.count();

        double totalEarnings = paymentRepository.findAll().stream()
            .filter(p -> p.getStatus() == Payment.PaymentStatus.SUCCESS)
            .mapToDouble(p -> p.getAmount().doubleValue())
            .sum();

        long activeRides = rideRepository.findAll().stream()
            .filter(r -> r.getStatus() == Ride.RideStatus.ACTIVE)
            .count();

        long cancelledRides = rideRepository.findAll().stream()
            .filter(r -> r.getStatus() == Ride.RideStatus.CANCELLED)
            .count();

        return new AdminStatsResponse(totalUsers, totalRides, totalBookings,
            totalPayments, totalEarnings, activeRides, cancelledRides);
    }

    public List<?> getAllUsers()    { return userRepository.findAll(); }
    public List<?> getAllRides()    { return rideRepository.findAll(); }
    public List<?> getAllPayments() { return paymentRepository.findAll(); }

    public void blockUser(Long userId) {
        userRepository.findById(userId).ifPresent(u -> {
            userRepository.delete(u);
        });
    }
}