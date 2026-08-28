package com.smartride.service;



public interface DistanceService {
    double getDistanceKm(double srcLat, double srcLng, double destLat, double destLng);
}