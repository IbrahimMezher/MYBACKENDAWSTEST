package com.flutterbackend.notifications.web;

import com.flutterbackend.notifications.dto.NotificationResponse;
import com.flutterbackend.notifications.service.NotificationService;
import com.flutterbackend.user.domain.User;
import com.flutterbackend.util.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUser currentUser;

    public NotificationController(NotificationService notificationService, CurrentUser currentUser) {
        this.notificationService = notificationService;
        this.currentUser = currentUser;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<NotificationResponse> getMine(HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return notificationService.getForUser(user);
    }

    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Long> unreadCount(HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return Map.of("count", notificationService.unreadCount(user));
    }

    @PutMapping("/{notificationId}/read")
    @PreAuthorize("isAuthenticated()")
    public String markRead(@PathVariable Long notificationId, HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return notificationService.markRead(notificationId, user);
    }

    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public String markAllRead(HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return notificationService.markAllRead(user);
    }
}
