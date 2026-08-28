package com.smartride.controller;

import com.smartride.dto.FareRequest;
import com.smartride.dto.FareResponse;
import com.smartride.service.FareService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fare")
public class FareController {

    @Autowired
    private FareService fareService;

    @PostMapping("/calculate")
    public ResponseEntity<FareResponse> calculateFare(@Valid @RequestBody FareRequest request) {
        return ResponseEntity.ok(fareService.calculateFare(request));
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<FareResponse> getFareByRide(@PathVariable Long rideId) {
        return ResponseEntity.ok(fareService.getFareByRideId(rideId));
    }
}