package com.smartride.service;

import com.smartride.dto.ReviewRequest;
import com.smartride.dto.ReviewResponse;
import com.smartride.exception.ResourceNotFoundException;
import com.smartride.model.Review;
import com.smartride.model.entity.Ride;
import com.smartride.model.User;
import com.smartride.repository.ReviewRepository;
import com.smartride.repository.RideRepository;
import com.smartride.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired private ReviewRepository reviewRepository;
    @Autowired private RideRepository rideRepository;
    @Autowired private UserRepository userRepository;

    @Override
    public ReviewResponse submitReview(ReviewRequest request) {
        Ride ride = rideRepository.findById(request.getRideId())
            .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));
        User reviewer = userRepository.findById(request.getReviewerId())
            .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));
        User reviewee = userRepository.findById(request.getRevieweeId())
            .orElseThrow(() -> new ResourceNotFoundException("Reviewee not found"));

        Review review = new Review(ride, reviewer, reviewee,
            request.getRating(), request.getComment());
        reviewRepository.save(review);

        return new ReviewResponse(review.getId(), reviewer.getEmail(),
                review.getRating(), review.getComment(), review.getCreatedAt());
    }

    @Override
    public List<ReviewResponse> getReviewsForUser(Long userId) {
        return reviewRepository.findByRevieweeId(userId).stream()
        		.map(r -> new ReviewResponse(r.getId(), r.getReviewer().getEmail(),
        		        r.getRating(), r.getComment(), r.getCreatedAt()))
        		 .collect(Collectors.toList());
    }

    @Override
    public Double getAverageRating(Long userId) {
        Double avg = reviewRepository.findAverageRatingByUserId(userId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }
}