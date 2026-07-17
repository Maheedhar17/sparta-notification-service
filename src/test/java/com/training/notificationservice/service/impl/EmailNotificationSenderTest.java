package com.training.notificationservice.service.impl;

import com.training.notificationservice.entity.Notification;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.exception.NotificationServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailNotificationSenderTest {

    @Mock
    private JavaMailSender mailSender;

    private static final String FROM_ADDRESS = "account@gmail.com";

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
        EmailNotificationSender sender = new EmailNotificationSender(mailSender, FROM_ADDRESS);
        assertThat(sender.getChannel()).isEqualTo(NotificationChannel.EMAIL);
    }

    @Test
    void sendsThroughJavaMailSenderOnSuccess() {
        EmailNotificationSender sender = new EmailNotificationSender(mailSender, FROM_ADDRESS);

        sender.send(emailNotification());

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void throwsNotificationServiceExceptionWhenMailSenderFails() {
        doThrow(new MailSendException("SMTP connection refused"))
                .when(mailSender).send(any(SimpleMailMessage.class));
        EmailNotificationSender sender = new EmailNotificationSender(mailSender, FROM_ADDRESS);

        assertThatThrownBy(() -> sender.send(emailNotification()))
                .isInstanceOf(NotificationServiceException.class)
                .hasMessageContaining("Email provider failure");
    }
}
