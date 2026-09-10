package lk.SmartClass.repository;

import lk.SmartClass.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByGuardianId(Long guardianId);
    boolean existsByUserId(Long userId);
    Optional<Student> findByUserId(Long userId);
    Optional<Student> findByUserUsername(String username);
}