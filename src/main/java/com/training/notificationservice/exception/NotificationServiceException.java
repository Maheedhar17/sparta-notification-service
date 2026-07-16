package com.training.notificationservice.exception;

/**
 * Base runtime exception for all Notification Service business-logic failures.
 * Channel-specific and validation failures should extend this so the
 * {@link GlobalExceptionHandler} can handle the whole hierarchy consistently.
 */
public class NotificationServiceException extends RuntimeException {

    public NotificationServiceException(String message) {
        super(message);
    }

    public NotificationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
