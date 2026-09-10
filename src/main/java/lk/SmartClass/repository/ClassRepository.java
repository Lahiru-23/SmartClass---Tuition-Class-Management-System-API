package lk.SmartClass.repository;

import lk.SmartClass.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    List<ClassEntity> findByTeacherId(Long teacherId);
    List<ClassEntity> findByTermId(Long termId);
}

