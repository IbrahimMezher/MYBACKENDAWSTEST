package com.flutterbackend.notifications.repository;

import com.flutterbackend.notifications.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
    long countByUser_UserIdAndReadFalse(Long userId);
    Optional<Notification> findByNotificationIdAndUser_UserId(Long notificationId, Long userId);
}
