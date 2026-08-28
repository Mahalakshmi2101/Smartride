package com.smartride.service;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OsrmDistanceService implements DistanceService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // OSRM public demo server - free, no API key needed
    private static final String OSRM_URL =
        "http://router.project-osrm.org/route/v1/driving/{lng1},{lat1};{lng2},{lat2}?overview=false";

    @Override
    public double getDistanceKm(double srcLat, double srcLng, double destLat, double destLng) {
        try {
            // Note: OSRM wants lng,lat order - not a typo
            String url = String.format(
                "http://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=false",
                srcLng, srcLat, destLng, destLat
            );

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            // OSRM returns distance in meters
            double distanceMeters = root
                .path("routes")
                .get(0)
                .path("distance")
                .asDouble();

            return Math.round((distanceMeters / 1000.0) * 100.0) / 100.0; // round to 2 decimal places

        } catch (ResourceAccessException e) {
            // OSRM server unreachable - don't fake a distance, throw clearly
            throw new RuntimeException("Distance service unavailable. Please try again later.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate distance: " + e.getMessage());
        }
    }
}