package com.training.notificationservice.service;

import com.training.notificationservice.dto.request.NotificationRequestDto;
import com.training.notificationservice.dto.response.NotificationResponseDto;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Business-logic contract for notifications, kept independent of HTTP and
 * persistence so it can be unit tested and reused by future ingestion
 * adapters (e.g. a Kafka listener) without any changes.
 */
public interface NotificationService {

    NotificationResponseDto createNotification(NotificationRequestDto request);

    NotificationResponseDto getNotificationById(UUID id);

    Page<NotificationResponseDto> searchNotifications(String recipient, NotificationStatus status,
                                                       NotificationChannel channel, Pageable pageable);
}
