package com.example.BizzBuy.controller;

import com.example.BizzBuy.model.Notification;
import com.example.BizzBuy.model.User;
import com.example.BizzBuy.service.NotificationService;
import com.example.BizzBuy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping("/my-notifications")
    public ResponseEntity<?> getMyNotifications(@RequestHeader("X-USER") String username) {
        try {
            User user = userService.requireByUsername(username);
            List<Notification> notifications = notificationService.getUserNotifications(user.getId());
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadNotifications(@RequestHeader("X-USER") String username) {
        try {
            User user = userService.requireByUsername(username);
            List<Notification> notifications = notificationService.getUnreadNotifications(user.getId());
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(@RequestHeader("X-USER") String username) {
        try {
            User user = userService.requireByUsername(username);
            long count = notificationService.getUnreadCount(user.getId());
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}/mark-read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id, @RequestHeader("X-USER") String username) {
        try {
            Notification notification = notificationService.markAsRead(id);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<?> markAllAsRead(@RequestHeader("X-USER") String username) {
        try {
            User user = userService.requireByUsername(username);
            notificationService.markAllAsRead(user.getId());
            Map<String, String> response = new HashMap<>();
            response.put("message", "All notifications marked as read");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
