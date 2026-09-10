package lk.SmartClass.repository;

import lk.SmartClass.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByClassEntityId(Long classId);
    Optional<Enrollment> findByStudentIdAndClassEntityId(Long studentId, Long classId);
    boolean existsByStudentIdAndClassEntityId(Long studentId, Long classId);
    int countByClassEntityIdAndStatus(Long classId, String status);
}