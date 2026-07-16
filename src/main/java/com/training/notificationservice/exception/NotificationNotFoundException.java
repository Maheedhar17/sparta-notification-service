package com.training.notificationservice.exception;

import java.util.UUID;

/**
 * Thrown when a notification lookup by id finds no matching row.
 * Handled by {@link GlobalExceptionHandler} as a 404 response.
 */
public class NotificationNotFoundException extends NotificationServiceException {

    public NotificationNotFoundException(UUID id) {
        super("Notification not found with id: " + id);
    }

    public NotificationNotFoundException(String message) {
        super(message);
    }
}
