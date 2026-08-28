package com.smartride.repository;

import com.smartride.model.Fare;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FareRepository extends JpaRepository<Fare, Long> {
	Optional<Fare> findByRideId(Long rideId);
}