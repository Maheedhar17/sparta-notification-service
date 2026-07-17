package com.training.notificationservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

/**
 * Inbound wire contract for the Email-specific creation endpoint. Kept separate
 * from the generic {@link NotificationRequestDto} so the email address format
 * can be validated up front, before it ever reaches the shared service layer.
 */
public class EmailNotificationRequestDto {

    @NotBlank(message = "recipient is required")
    @Email(message = "recipient must be a valid email address")
    private String recipient;

    @NotBlank(message = "subject is required")
    private String subject;

    @NotBlank(message = "message is required")
    private String message;

    private UUID templateId;

    public EmailNotificationRequestDto() {
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
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
