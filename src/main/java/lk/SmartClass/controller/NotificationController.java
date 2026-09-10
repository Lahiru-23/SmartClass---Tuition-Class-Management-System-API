package lk.SmartClass.controller;

import lk.SmartClass.dto.response.NotificationResponse;
import lk.SmartClass.security.AccessGuard;
import lk.SmartClass.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final AccessGuard accessGuard;

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationResponse>> forUser(@PathVariable Long userId, Authentication authentication) {
        accessGuard.requireSelfUserOrStaff(userId, authentication);
        return ResponseEntity.ok(notificationService.getForUser(userId));
    }

    @GetMapping("/user/{userId}/unread")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationResponse>> unread(@PathVariable Long userId, Authentication authentication) {
        accessGuard.requireSelfUserOrStaff(userId, authentication);
        return ResponseEntity.ok(notificationService.getUnreadForUser(userId));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markRead(@PathVariable Long id, Authentication authentication) {
        notificationService.markAsRead(id, authentication);
        return ResponseEntity.noContent().build();
    }
}