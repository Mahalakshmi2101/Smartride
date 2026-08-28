package com.smartride.service;

import com.smartride.dto.ReviewRequest;
import com.smartride.dto.ReviewResponse;
import java.util.List;

public interface ReviewService {
    ReviewResponse submitReview(ReviewRequest request);
    List<ReviewResponse> getReviewsForUser(Long userId);
    Double getAverageRating(Long userId);
}