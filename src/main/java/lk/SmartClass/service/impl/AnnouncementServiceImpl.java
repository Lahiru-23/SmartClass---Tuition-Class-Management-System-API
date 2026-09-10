package lk.SmartClass.service.impl;

import lk.SmartClass.dto.request.AnnouncementRequest;
import lk.SmartClass.dto.response.AnnouncementResponse;
import lk.SmartClass.entity.*;
import lk.SmartClass.exception.BadRequestException;
import lk.SmartClass.exception.ResourceNotFoundException;
import lk.SmartClass.repository.*;
import lk.SmartClass.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private static final Logger log = LoggerFactory.getLogger(AnnouncementServiceImpl.class);

    private final AnnouncementRepository announcementRepository;
    private final ClassRepository classRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public AnnouncementResponse post(AnnouncementRequest request, String postedByUsername) {
        User poster = userRepository.findByUsername(postedByUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Posting user not found"));

        ClassEntity classEntity = null;
        if (request.getClassId() != null) {
            classEntity = classRepository.findById(request.getClassId())
                    .orElseThrow(() -> new BadRequestException("Invalid classId: " + request.getClassId()));
        }

        Announcement announcement = Announcement.builder()
                .classEntity(classEntity).postedBy(poster)
                .title(request.getTitle()).body(request.getBody()).build();

        Announcement saved = announcementRepository.save(announcement);
        log.info("Announcement posted: id={}, class={}, by={}",
                saved.getId(), classEntity != null ? classEntity.getId() : "INSTITUTE_WIDE", postedByUsername);

        if (classEntity != null) {
            List<Enrollment> activeEnrollments = enrollmentRepository.findByClassEntityId(classEntity.getId())
                    .stream().filter(e -> "ACTIVE".equals(e.getStatus())).toList();

            for (Enrollment e : activeEnrollments) {
                User studentUser = e.getStudent().getUser();
                if (studentUser != null) {
                    Notification notification = Notification.builder()
                            .user(studentUser)
                            .message("New announcement in " + classEntity.getName() + ": " + request.getTitle())
                            .type("ANNOUNCEMENT").build();
                    notificationRepository.save(notification);
                }
            }
            log.info("Notified {} students for announcement id={}", activeEnrollments.size(), saved.getId());
        }

        return toResponse(saved);
    }

    @Override
    public List<AnnouncementResponse> getByClass(Long classId) {
        return announcementRepository.findByClassEntityIdOrderByPostedAtDesc(classId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<AnnouncementResponse> getInstituteWide() {
        return announcementRepository.findByClassEntityIsNullOrderByPostedAtDesc().stream().map(this::toResponse).toList();
    }

    private AnnouncementResponse toResponse(Announcement a) {
        return AnnouncementResponse.builder()
                .id(a.getId())
                .classId(a.getClassEntity() != null ? a.getClassEntity().getId() : null)
                .className(a.getClassEntity() != null ? a.getClassEntity().getName() : "Institute-wide")
                .postedByUsername(a.getPostedBy().getUsername())
                .title(a.getTitle()).body(a.getBody()).postedAt(a.getPostedAt())
                .build();
    }
}
