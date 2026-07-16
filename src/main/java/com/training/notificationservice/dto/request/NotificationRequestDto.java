package com.training.notificationservice.dto.request;

import com.training.notificationservice.enums.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Inbound wire contract for creating a notification. Kept separate from the
 * {@code Notification} entity so the persistence model can evolve without
 * breaking API consumers (Order Service, other callers).
 */
public class NotificationRequestDto {

    @NotBlank(message = "recipient is required")
    private String recipient;

    @NotNull(message = "channel is required")
    private NotificationChannel channel;

    private String subject;

    @NotBlank(message = "message is required")
    private String message;

    private UUID templateId;

    public NotificationRequestDto() {
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UUID getTemplateId() {
        return templateId;
    }

    public void setTemplateId(UUID templateId) {
        this.templateId = templateId;
    }
}
