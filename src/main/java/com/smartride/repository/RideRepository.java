package com.smartride.repository;

import com.smartride.model.User;
import com.smartride.model.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByDriver(User driver);
    List<Ride> findByStatus(Ride.RideStatus status);
}