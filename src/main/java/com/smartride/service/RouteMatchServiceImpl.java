package com.smartride.service;

import com.smartride.dto.RouteMatchResponse;
import com.smartride.model.entity.Ride;
import com.smartride.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteMatchServiceImpl implements RouteMatchService {

    @Autowired
    private RideRepository rideRepository;

    @Override
    public List<RouteMatchResponse> findDirectMatches(String source, String destination) {
        // Exact source+destination match — available rides only
        return rideRepository.findAll().stream()
            .filter(ride -> ride.getSource() != null && ride.getDestination() != null)
            .filter(ride ->
                ride.getSource().equalsIgnoreCase(source) &&
                ride.getDestination().equalsIgnoreCase(destination))
            .filter(ride -> ride.getAvailableSeats() > 0)
            .map(ride -> toResponse(ride, "DIRECT"))
            .collect(Collectors.toList());
    }

    @Override
    public List<RouteMatchResponse> findAllMatches(String source, String destination) {
        // Direct matches + partial (driver's route passes through passenger's source)
        List<RouteMatchResponse> direct = findDirectMatches(source, destination);

        List<RouteMatchResponse> partial = rideRepository.findAll().stream()
            .filter(ride -> ride.getSource() != null && ride.getDestination() != null)
            .filter(ride ->
                ride.getSource().equalsIgnoreCase(source) &&
                !ride.getDestination().equalsIgnoreCase(destination))
            .filter(ride -> ride.getAvailableSeats() > 0)
            .map(ride -> toResponse(ride, "PARTIAL"))
            .collect(Collectors.toList());

        direct.addAll(partial);
        return direct;
    }

    private RouteMatchResponse toResponse(Ride ride, String matchType) {
    	String driverName = ride.getDriver() != null
    	        ? ride.getDriver().getEmail()
    	        : "Unknown";

        return new RouteMatchResponse(
            ride.getId(),
            driverName,
            ride.getSource(),
            ride.getDestination(),
            ride.getRideTime() != null ? ride.getRideTime().toString() : "N/A",
            ride.getAvailableSeats(),
            matchType
        );
    }
}