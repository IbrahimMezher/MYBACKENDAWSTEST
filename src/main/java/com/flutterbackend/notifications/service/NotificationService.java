package com.flutterbackend.notifications.service;

import com.flutterbackend.notifications.domain.Notification;
import com.flutterbackend.notifications.dto.NotificationResponse;
import com.flutterbackend.notifications.repository.NotificationRepository;
import com.flutterbackend.user.domain.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<NotificationResponse> getForUser(User user) {
        return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public long unreadCount(User user) {
        return notificationRepository.countByUser_UserIdAndReadFalse(user.getUserId());
    }

    public String markRead(Long notificationId, User user) {
        Notification notification = notificationRepository
                .findByNotificationIdAndUser_UserId(notificationId, user.getUserId())
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
        return "Notification marked as read.";
    }

    public String markAllRead(User user) {
        List<Notification> notifications =
                notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId());
        LocalDateTime now = LocalDateTime.now();
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                notification.setRead(true);
                notification.setReadAt(now);
            }
        }
        notificationRepository.saveAll(notifications);
        return "Notifications marked as read.";
    }

    public void create(User user, String title, String message, String type) {
        if (user == null) return;
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type == null || type.isBlank() ? "GENERAL" : type);
        notificationRepository.save(notification);
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt() != null ? notification.getCreatedAt().toString() : null)
                .readAt(notification.getReadAt() != null ? notification.getReadAt().toString() : null)
                .build();
    }
}
