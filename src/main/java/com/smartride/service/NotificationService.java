package com.smartride.service;

import com.smartride.dto.NotificationResponse;
import com.smartride.model.Notification;
import java.util.List;

public interface NotificationService {
    void sendNotification(Long userId, String message, Notification.NotificationType type);
    List<NotificationResponse> getNotifications(Long userId);
    List<NotificationResponse> getUnreadNotifications(Long userId);
    long getUnreadCount(Long userId);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
}