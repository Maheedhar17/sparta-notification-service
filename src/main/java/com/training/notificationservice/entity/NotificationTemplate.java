package com.training.notificationservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Skeleton only - every other field (name, channel, subjectTemplate,
 * bodyTemplate, timestamps) is Developer 5's responsibility per the task
 * breakdown. The id is included here because a JPA {@code @Entity} without
 * an {@code @Id} fails Hibernate's startup validation for the whole app,
 * not just this class - it's a structural requirement, not a business field.
 */
@Entity
@Table(name = "notification_templates")
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
