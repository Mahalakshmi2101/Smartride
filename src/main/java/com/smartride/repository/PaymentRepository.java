package com.smartride.repository;

import com.smartride.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPassengerId(Long passengerId);
    List<Payment> findByRideId(Long rideId);
}