package com.training.notificationservice.service.impl;

import com.training.notificationservice.dto.request.NotificationRequestDto;
import com.training.notificationservice.dto.response.NotificationResponseDto;
import com.training.notificationservice.entity.Notification;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.enums.NotificationStatus;
import com.training.notificationservice.exception.NotificationNotFoundException;
import com.training.notificationservice.repository.NotificationRepository;
import com.training.notificationservice.service.NotificationSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationSender emailSender;

    private NotificationRequestDto validRequest() {
        NotificationRequestDto request = new NotificationRequestDto();
        request.setRecipient("jane.doe@example.com");
        request.setChannel(NotificationChannel.EMAIL);
        request.setSubject("Order confirmed");
        request.setMessage("Your order has shipped");
        return request;
    }

    @Test
    void createNotification_withNoSenderRegistered_persistsAsPending() {
        NotificationServiceImpl service =
                new NotificationServiceImpl(notificationRepository, Collections.emptyList());
        NotificationRequestDto request = validRequest();
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NotificationResponseDto result = service.createNotification(request);

        assertThat(result.getStatus()).isEqualTo(NotificationStatus.PENDING);
        assertThat(result.getRecipient()).isEqualTo(request.getRecipient());
        assertThat(result.getChannel()).isEqualTo(NotificationChannel.EMAIL);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createNotification_whenSenderThrows_marksFailedAndIncrementsRetryCount() {
        when(emailSender.getChannel()).thenReturn(NotificationChannel.EMAIL);
        doThrow(new RuntimeException("provider unreachable")).when(emailSender).send(any(Notification.class));
        NotificationServiceImpl service =
                new NotificationServiceImpl(notificationRepository, List.of(emailSender));
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NotificationResponseDto result = service.createNotification(validRequest());

        assertThat(result.getStatus()).isEqualTo(NotificationStatus.FAILED);
        assertThat(result.getRetryCount()).isEqualTo(1);
    }

    @Test
    void getNotificationById_whenFound_returnsMappedDto() {
        NotificationServiceImpl service =
                new NotificationServiceImpl(notificationRepository, Collections.emptyList());
        UUID id = UUID.randomUUID();
        Notification entity = new Notification();
        entity.setId(id);
        entity.setRecipient("jane.doe@example.com");
        entity.setChannel(NotificationChannel.EMAIL);
        entity.setMessage("hello");
        when(notificationRepository.findById(id)).thenReturn(Optional.of(entity));

        NotificationResponseDto result = service.getNotificationById(id);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getRecipient()).isEqualTo("jane.doe@example.com");
    }

    @Test
    void getNotificationById_whenMissing_throwsNotFound() {
        NotificationServiceImpl service =
                new NotificationServiceImpl(notificationRepository, Collections.emptyList());
        UUID id = UUID.randomUUID();
        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getNotificationById(id))
                .isInstanceOf(NotificationNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}
