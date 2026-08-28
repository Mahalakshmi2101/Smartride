package com.smartride.controller;

import com.smartride.dto.ReviewRequest;
import com.smartride.dto.ReviewResponse;
import com.smartride.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired private ReviewService reviewService;

    @PostMapping("/submit")
    public ResponseEntity<ReviewResponse> submit(@Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.submitReview(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsForUser(userId));
    }

    @GetMapping("/user/{userId}/rating")
    public ResponseEntity<Map<String, Double>> getRating(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of("averageRating", reviewService.getAverageRating(userId)));
    }
}