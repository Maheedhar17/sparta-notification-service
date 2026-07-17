package com.training.notificationservice.service.impl;

import com.training.notificationservice.entity.Notification;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.exception.NotificationServiceException;
import com.training.notificationservice.service.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Mock email provider adapter - no real SES/SendGrid call is made (out of
 * scope for this phase). Simulates provider outcomes via a configurable
 * failure rate so {@code NotificationServiceImpl}'s PENDING/SENT/FAILED
 * transition and retry count can be exercised end to end without a real
 * provider account.
 */
@Component
public class EmailNotificationSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationSender.class);

    private final double failureRate;

    public EmailNotificationSender(@Value("${notification.email.mock.failure-rate:0.1}") double failureRate) {
        this.failureRate = failureRate;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public void send(Notification notification) {
        String maskedRecipient = mask(notification.getRecipient());
        double roll = ThreadLocalRandom.current().nextDouble();

        if (roll < failureRate) {
            log.warn("Simulated email send failure for recipient={}", maskedRecipient);
            throw new NotificationServiceException(
                    "Simulated email provider failure for recipient " + maskedRecipient);
        }

        log.info("Simulated email sent to recipient={}", maskedRecipient);
    }

    private String mask(String email) {
        if (email == null) {
            return "***";
        }
        int at = email.indexOf('@');
        if (at <= 1) {
            return "***";
        }
        return email.charAt(0) + "***" + email.substring(at);
    }
}
