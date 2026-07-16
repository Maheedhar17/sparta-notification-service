package com.training.notificationservice.service;

import com.training.notificationservice.entity.Notification;
import com.training.notificationservice.enums.NotificationChannel;

/**
 * Strategy interface for channel-specific dispatch. Developers 2-4 each add a
 * {@code @Component} implementation (Email/SMS/In-App) without ever touching
 * this interface or {@code NotificationServiceImpl} - new channels plug in by
 * addition, not by modification (Open/Closed Principle).
 */
public interface NotificationSender {

    /**
     * @return the channel this sender handles, used by the service layer to
     * pick the right implementation out of all registered senders.
     */
    NotificationChannel getChannel();

    /**
     * Attempts delivery for the given notification. Implementations should
     * throw {@link com.training.notificationservice.exception.NotificationServiceException}
     * (or a subtype) on failure so the caller can react consistently.
     */
    void send(Notification notification);
}
