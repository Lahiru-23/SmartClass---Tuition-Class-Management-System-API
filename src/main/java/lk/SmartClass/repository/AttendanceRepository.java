package lk.SmartClass.repository;

import lk.SmartClass.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findByEnrollmentId(Long enrollmentId);
    List<AttendanceRecord> findByEnrollmentClassEntityIdAndDate(Long classId, LocalDate date);
    boolean existsByEnrollmentIdAndDate(Long enrollmentId, LocalDate date);
}