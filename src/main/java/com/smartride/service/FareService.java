package com.smartride.service;


import com.smartride.dto.FareRequest;
import com.smartride.dto.FareResponse;

public interface FareService {
    FareResponse calculateFare(FareRequest request);
    FareResponse getFareByRideId(Long rideId);
}