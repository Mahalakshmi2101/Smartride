package com.smartride.dto;

import java.time.LocalDateTime;

public class ReviewResponse {
    private Long id;
    private String reviewerName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    private Double averageRating;

    public ReviewResponse() {}

    public ReviewResponse(Long id, String reviewerName, int rating,
                          String comment, LocalDateTime createdAt) {
        this.id = id;
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getReviewerName() { return reviewerName; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
}