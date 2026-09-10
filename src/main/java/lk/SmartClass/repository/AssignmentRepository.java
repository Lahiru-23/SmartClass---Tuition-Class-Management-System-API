package lk.SmartClass.repository;

import lk.SmartClass.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByClassEntityId(Long classId);
}
