package lk.SmartClass.repository;

import lk.SmartClass.entity.ClassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
    List<ClassSchedule> findByClassEntityId(Long classId);
}
