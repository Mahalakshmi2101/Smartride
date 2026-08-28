package com.smartride.controller;

import com.smartride.dto.RouteMatchResponse;
import com.smartride.service.RouteMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    @Autowired
    private RouteMatchService routeMatchService;

    @GetMapping("/match")
    public ResponseEntity<List<RouteMatchResponse>> findMatches(
        @RequestParam String source,
        @RequestParam String destination,
        @RequestParam(defaultValue = "false") boolean includePartial) {

        if (includePartial) {
            return ResponseEntity.ok(routeMatchService.findAllMatches(source, destination));
        }
        return ResponseEntity.ok(routeMatchService.findDirectMatches(source, destination));
    }
}