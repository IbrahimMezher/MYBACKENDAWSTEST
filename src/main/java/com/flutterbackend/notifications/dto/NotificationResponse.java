package com.flutterbackend.notifications.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponse {
    private Long notificationId;
    private String title;
    private String message;
    private String type;
    private boolean read;
    private String createdAt;
    private String readAt;
}
