package com.smartride.dto;


public class RouteMatchResponse {
    private Long rideId;
    private String driverName;
    private String source;
    private String destination;
    private String departureTime;
    private int availableSeats;
    private String matchType; // "DIRECT" or "PARTIAL"

    public RouteMatchResponse() {}

    public RouteMatchResponse(Long rideId, String driverName, String source,
                               String destination, String departureTime,
                               int availableSeats, String matchType) {
        this.rideId = rideId;
        this.driverName = driverName;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
        this.matchType = matchType;
    }

    public Long getRideId() { return rideId; }
    public String getDriverName() { return driverName; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public String getRideTime() { return departureTime; }
    public int getAvailableSeats() { return availableSeats; }
    public String getMatchType() { return matchType; }
}