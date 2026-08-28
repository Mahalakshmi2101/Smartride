package com.smartride.dto;

import java.time.LocalDateTime;

public class NotificationResponse {
    private Long id;
    private String message;
    private String type;
    private boolean read;
    private LocalDateTime createdAt;

    public NotificationResponse() {}

    public NotificationResponse(Long id, String message, String type,
                                 boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.message = message;
        this.type = type;
        this.read = read;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public boolean isRead() { return read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}