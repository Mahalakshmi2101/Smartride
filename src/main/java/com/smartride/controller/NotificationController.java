package com.smartride.controller;

import com.smartride.dto.NotificationResponse;
import com.smartride.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired private NotificationService notificationService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationResponse>> getAll(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotifications(userId));
    }

    @GetMapping("/{userId}/unread")
    public ResponseEntity<List<NotificationResponse>> getUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    @GetMapping("/{userId}/count")
    public ResponseEntity<Map<String, Long>> getCount(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of("unread", notificationService.getUnreadCount(userId)));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String> markRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok("Marked as read");
    }

    @PutMapping("/{userId}/read-all")
    public ResponseEntity<String> markAllRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok("All marked as read");
    }
}