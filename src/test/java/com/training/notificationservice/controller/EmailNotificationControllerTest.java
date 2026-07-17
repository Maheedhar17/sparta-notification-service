package com.training.notificationservice.controller;

import com.training.notificationservice.dto.response.NotificationResponseDto;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.enums.NotificationStatus;
import com.training.notificationservice.exception.GlobalExceptionHandler;
import com.training.notificationservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmailNotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        EmailNotificationController controller = new EmailNotificationController(notificationService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private NotificationResponseDto emailResponse(NotificationStatus status) {
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setId(UUID.randomUUID());
        dto.setRecipient("jane.doe@example.com");
        dto.setChannel(NotificationChannel.EMAIL);
        dto.setSubject("Your order is confirmed");
        dto.setMessage("Hi Jane, your order has been confirmed.");
        dto.setStatus(status);
        dto.setRetryCount(0);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        return dto;
    }

    @Test
    void createEmailNotificationReturns201WithSentBody() throws Exception {
        when(notificationService.createNotification(any())).thenReturn(emailResponse(NotificationStatus.SENT));

        String body = """
                {
                  "recipient": "jane.doe@example.com",
                  "subject": "Your order is confirmed",
                  "message": "Hi Jane, your order has been confirmed."
                }
                """;

        mockMvc.perform(post("/api/v1/notifications/email")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.channel").value("EMAIL"))
                .andExpect(jsonPath("$.status").value("SENT"));
    }

    @Test
    void createEmailNotificationWithInvalidEmailReturns400() throws Exception {
        String body = """
                {
                  "recipient": "not-an-email",
                  "subject": "Your order is confirmed",
                  "message": "Hi Jane, your order has been confirmed."
                }
                """;

        mockMvc.perform(post("/api/v1/notifications/email")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEmailNotificationWithBlankSubjectReturns400() throws Exception {
        String body = """
                {
                  "recipient": "jane.doe@example.com",
                  "subject": "",
                  "message": "Hi Jane, your order has been confirmed."
                }
                """;

        mockMvc.perform(post("/api/v1/notifications/email")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEmailNotificationWithBlankMessageReturns400() throws Exception {
        String body = """
                {
                  "recipient": "jane.doe@example.com",
                  "subject": "Your order is confirmed",
                  "message": ""
                }
                """;

        mockMvc.perform(post("/api/v1/notifications/email")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
