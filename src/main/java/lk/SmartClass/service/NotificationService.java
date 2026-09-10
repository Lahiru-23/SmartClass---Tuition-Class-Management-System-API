package lk.SmartClass.service;

import lk.SmartClass.dto.response.NotificationResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getForUser(Long userId);
    List<NotificationResponse> getUnreadForUser(Long userId);
    void markAsRead(Long id, Authentication authentication);
}
