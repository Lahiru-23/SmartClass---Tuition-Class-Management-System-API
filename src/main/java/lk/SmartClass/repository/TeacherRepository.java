package lk.SmartClass.repository;

import lk.SmartClass.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUserId(Long userId);
    Optional<Teacher> findByUserUsername(String username);
    boolean existsByUserId(Long userId);
}