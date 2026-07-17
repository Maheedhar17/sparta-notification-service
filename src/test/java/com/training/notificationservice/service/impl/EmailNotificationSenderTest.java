package com.training.notificationservice.service.impl;

import com.training.notificationservice.entity.Notification;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.exception.NotificationServiceException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailNotificationSenderTest {

    private Notification emailNotification() {
        Notification notification = new Notification();
        notification.setRecipient("jane.doe@example.com");
        notification.setChannel(NotificationChannel.EMAIL);
        notification.setSubject("Your order is confirmed");
        notification.setMessage("Hi Jane, your order has been confirmed.");
        return notification;
    }

    @Test
    void reportsEmailChannel() {
        EmailNotificationSender sender = new EmailNotificationSender(0.0);
        assertThat(sender.getChannel()).isEqualTo(NotificationChannel.EMAIL);
    }

    @Test
    void sendsSuccessfullyWhenFailureRateIsZero() {
        EmailNotificationSender sender = new EmailNotificationSender(0.0);
        assertThatCode(() -> sender.send(emailNotification())).doesNotThrowAnyException();
    }

    @Test
    void throwsNotificationServiceExceptionWhenFailureRateIsOne() {
        EmailNotificationSender sender = new EmailNotificationSender(1.0);
        assertThatThrownBy(() -> sender.send(emailNotification()))
                .isInstanceOf(NotificationServiceException.class)
                .hasMessageContaining("Simulated email provider failure");
    }
}
