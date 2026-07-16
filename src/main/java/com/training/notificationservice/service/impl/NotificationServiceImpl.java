package com.training.notificationservice.service.impl;

import com.training.notificationservice.dto.request.NotificationRequestDto;
import com.training.notificationservice.dto.response.NotificationResponseDto;
import com.training.notificationservice.entity.Notification;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.enums.NotificationStatus;
import com.training.notificationservice.exception.NotificationNotFoundException;
import com.training.notificationservice.repository.NotificationRepository;
import com.training.notificationservice.repository.NotificationSpecifications;
import com.training.notificationservice.service.NotificationSender;
import com.training.notificationservice.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Orchestrates notification creation, lookup, and search.
 * <p>
 * Dispatch uses the Strategy pattern: {@link NotificationSender} implementations
 * register themselves as Spring beans (one per {@link NotificationChannel}), and
 * this class picks the right one at runtime via {@code sendersByChannel}. Adding
 * a new channel means adding a new {@code @Component}, not editing this class
 * (Open/Closed Principle) - which is what lets Developers 2-4 build their slices
 * without ever touching Developer 1's files.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final Map<NotificationChannel, NotificationSender> sendersByChannel;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                    List<NotificationSender> senders) {
        this.notificationRepository = notificationRepository;
        this.sendersByChannel = senders.stream()
                .collect(Collectors.toMap(NotificationSender::getChannel, Function.identity()));
    }

    @Override
    @Transactional
    public NotificationResponseDto createNotification(NotificationRequestDto request) {
        log.info("Creating {} notification for recipient={}", request.getChannel(), mask(request.getRecipient()));

        Notification notification = toEntity(request);
        Notification saved = notificationRepository.save(notification);

        dispatch(saved);

        log.info("Notification {} persisted with status={}", saved.getId(), saved.getStatus());
        return toResponseDto(saved);
    }

    @Override
    public NotificationResponseDto getNotificationById(UUID id) {
        log.debug("Looking up notification {}", id);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Notification {} not found", id);
                    return new NotificationNotFoundException(id);
                });
        return toResponseDto(notification);
    }

    @Override
    public Page<NotificationResponseDto> searchNotifications(String recipient, NotificationStatus status,
                                                               NotificationChannel channel, Pageable pageable) {
        log.debug("Searching notifications recipient={}, status={}, channel={}, page={}",
                recipient == null ? null : mask(recipient), status, channel, pageable);
        return notificationRepository
                .findAll(NotificationSpecifications.filterBy(recipient, status, channel), pageable)
                .map(this::toResponseDto);
    }

    /**
     * Attempts delivery through the sender registered for this notification's
     * channel. If no channel implementation has been added yet (Developers 2-4's
     * PRs not merged), the notification is simply left in its current status
     * rather than treated as an error.
     */
    private void dispatch(Notification notification) {
        NotificationSender sender = sendersByChannel.get(notification.getChannel());
        if (sender == null) {
            log.info("No sender registered yet for channel={}; leaving notification {} as {}",
                    notification.getChannel(), notification.getId(), notification.getStatus());
            return;
        }
        try {
            sender.send(notification);
            notification.setStatus(NotificationStatus.SENT);
        } catch (Exception ex) {
            log.error("Dispatch failed for notification {} on channel {}: {}",
                    notification.getId(), notification.getChannel(), ex.getMessage(), ex);
            notification.setRetryCount(notification.getRetryCount() + 1);
            notification.setStatus(NotificationStatus.FAILED);
        }
        notificationRepository.save(notification);
    }

    private Notification toEntity(NotificationRequestDto request) {
        Notification notification = new Notification();
        notification.setRecipient(request.getRecipient());
        notification.setChannel(request.getChannel());
        notification.setSubject(request.getSubject());
        notification.setMessage(request.getMessage());
        notification.setTemplateId(request.getTemplateId());
        return notification;
    }

    private NotificationResponseDto toResponseDto(Notification notification) {
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setId(notification.getId());
        dto.setRecipient(notification.getRecipient());
        dto.setChannel(notification.getChannel());
        dto.setSubject(notification.getSubject());
        dto.setMessage(notification.getMessage());
        dto.setTemplateId(notification.getTemplateId());
        dto.setStatus(notification.getStatus());
        dto.setRetryCount(notification.getRetryCount());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setUpdatedAt(notification.getUpdatedAt());
        return dto;
    }

    /** Masks a recipient (email/phone) so PII never appears in full in logs. */
    private String mask(String recipient) {
        if (recipient == null || recipient.length() <= 2) {
            return "***";
        }
        int visible = Math.min(2, recipient.length() - 1);
        return recipient.substring(0, visible) + "***";
    }
}
