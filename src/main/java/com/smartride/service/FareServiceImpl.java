package com.smartride.service;

import com.smartride.dto.FareRequest;
import com.smartride.dto.FareResponse;
import com.smartride.exception.FareConfigNotFoundException;
import com.smartride.exception.ResourceNotFoundException;
import com.smartride.model.FareConfig;
import com.smartride.repository.FareConfigRepository;
import com.smartride.repository.FareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class FareServiceImpl implements FareService {

    @Autowired
    private FareConfigRepository fareConfigRepository;

    @Autowired
    private FareRepository fareRepository;

    @Autowired
    private DistanceService distanceService;

    @Override
    public FareResponse calculateFare(FareRequest request) {

        // Step 1: Get active fare config from DB
        FareConfig config = fareConfigRepository.findByActiveTrue()
            .orElseThrow(FareConfigNotFoundException::new);

        // Step 2: Get real road distance from OSRM
        double distanceKm = distanceService.getDistanceKm(
            request.getSourceLat(), request.getSourceLng(),
            request.getDestLat(), request.getDestLng()
        );

        // Step 3: Fare = Base Fare + (Rate per Km × Distance)
        BigDecimal distanceCost = config.getRatePerKm()
            .multiply(BigDecimal.valueOf(distanceKm));

        BigDecimal totalFare = config.getBaseFare()
            .add(distanceCost)
            .setScale(2, RoundingMode.HALF_UP);

        // Step 4: Split cost equally among passengers
        BigDecimal perPassengerFare = totalFare.divide(
            BigDecimal.valueOf(request.getPassengerCount()), 2, RoundingMode.HALF_UP
        );

        return new FareResponse(distanceKm, totalFare, perPassengerFare, request.getPassengerCount());
    }

    @Override
    public FareResponse getFareByRideId(Long rideId) {
        var fare = fareRepository.findByRideId(rideId)
            .orElseThrow(() -> new ResourceNotFoundException("Fare not found for ride ID: " + rideId));

        return new FareResponse(
            fare.getDistanceKm(),
            fare.getTotalFare(),
            fare.getPerPassengerFare(),
            fare.getPassengerCount()
        );
    }
}