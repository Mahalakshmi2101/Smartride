package com.smartride.scheduler;

import com.smartride.model.entity.Ride;
import com.smartride.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class RideScheduler {

    @Autowired
    private RideRepository rideRepository;

    // runs every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void markExpiredRidesCompleted() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        List<Ride> activeRides = rideRepository.findByStatus(Ride.RideStatus.ACTIVE);
        for (Ride ride : activeRides) {
            LocalDateTime rideDateTime = LocalDateTime.of(ride.getRideDate(), ride.getRideTime());
            if (rideDateTime.isBefore(now)) {
                ride.setStatus(Ride.RideStatus.COMPLETED);
                rideRepository.save(ride);
            }
        }
    }
}