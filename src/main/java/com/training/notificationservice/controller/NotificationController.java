package com.training.notificationservice.controller;

import com.training.notificationservice.dto.request.NotificationRequestDto;
import com.training.notificationservice.dto.response.NotificationResponseDto;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.enums.NotificationStatus;
import com.training.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Reference REST controller for the core notification contract. Thin by
 * design - all business logic lives in {@link NotificationService}, so this
 * class only translates HTTP concerns (status codes, path/query params) to
 * and from the service layer.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Core notification create/read operations")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @Operation(summary = "Create and enqueue a notification")
    public ResponseEntity<NotificationResponseDto> createNotification(
            @Valid @RequestBody NotificationRequestDto request) {
        log.info("POST /api/v1/notifications channel={}", request.getChannel());
        NotificationResponseDto created = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Fetch a single notification by id")
    public ResponseEntity<NotificationResponseDto> getNotificationById(@PathVariable UUID id) {
        log.info("GET /api/v1/notifications/{}", id);
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @GetMapping
    @Operation(summary = "Search notifications with optional filters and pagination")
    public ResponseEntity<Page<NotificationResponseDto>> searchNotifications(
            @RequestParam(required = false) String recipient,
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(required = false) NotificationChannel channel,
            Pageable pageable) {
        log.info("GET /api/v1/notifications status={}, channel={}, page={}", status, channel, pageable);
        return ResponseEntity.ok(notificationService.searchNotifications(recipient, status, channel, pageable));
    }
}
