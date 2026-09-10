package lk.SmartClass.repository;

import lk.SmartClass.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByClassEntityIdOrderByPostedAtDesc(Long classId);
    List<Announcement> findByClassEntityIsNullOrderByPostedAtDesc();
}

