package lk.SmartClass.service.impl;

import lk.SmartClass.dto.response.NotificationResponse;
import lk.SmartClass.entity.Notification;
import lk.SmartClass.exception.ResourceNotFoundException;
import lk.SmartClass.repository.NotificationRepository;
import lk.SmartClass.security.AccessGuard;
import lk.SmartClass.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final AccessGuard accessGuard;

    @Override
    public List<NotificationResponse> getForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<NotificationResponse> getUnreadForUser(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void markAsRead(Long id, Authentication authentication) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        accessGuard.requireSelfUserOrStaff(notification.getUser().getId(), authentication);
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId()).message(n.getMessage()).type(n.getType())
                .isRead(n.isRead()).createdAt(n.getCreatedAt()).build();
    }
}
