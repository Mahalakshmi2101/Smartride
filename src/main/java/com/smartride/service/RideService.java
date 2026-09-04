package com.smartride.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartride.model.entity.Ride;
import com.smartride.repository.RideRepository;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    public List<Ride> searchRides(String source, String destination,
                                   String tripType, Boolean womenOnly) {

        LocalDateTime nowIST = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));

        return rideRepository.findByStatus(Ride.RideStatus.ACTIVE).stream()
            .filter(r -> {
                LocalDateTime rideDateTime = LocalDateTime.of(r.getRideDate(), r.getRideTime());
                return rideDateTime.isAfter(nowIST);
            })
            .filter(r -> r.getAvailableSeats() > 0)
            .filter(r -> source == null || r.getSource().equalsIgnoreCase(source))
            .filter(r -> destination == null || r.getDestination().equalsIgnoreCase(destination))
            .filter(r -> tripType == null || tripType.equalsIgnoreCase(r.getTripType()))
            .filter(r -> womenOnly == null || !womenOnly || r.isWomenOnly())
            .collect(Collectors.toList());
    }
}