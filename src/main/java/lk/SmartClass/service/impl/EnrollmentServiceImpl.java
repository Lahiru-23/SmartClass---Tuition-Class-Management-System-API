package lk.SmartClass.service.impl;

import lk.SmartClass.dto.request.EnrollmentRequest;
import lk.SmartClass.dto.response.EnrollmentResponse;
import lk.SmartClass.entity.ClassEntity;
import lk.SmartClass.entity.Enrollment;
import lk.SmartClass.entity.Student;
import lk.SmartClass.exception.BadRequestException;
import lk.SmartClass.exception.ResourceNotFoundException;
import lk.SmartClass.repository.ClassRepository;
import lk.SmartClass.repository.EnrollmentRepository;
import lk.SmartClass.repository.StudentRepository;
import lk.SmartClass.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentServiceImpl.class);
    private static final Set<String> VALID_STATUSES = Set.of("ACTIVE", "DROPPED", "COMPLETED");

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;

    @Override
    @Transactional
    public EnrollmentResponse enroll(EnrollmentRequest request) {
        if (enrollmentRepository.existsByStudentIdAndClassEntityId(request.getStudentId(), request.getClassId())) {
            throw new BadRequestException("Student is already enrolled in this class");
        }
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new BadRequestException("Invalid studentId: " + request.getStudentId()));
        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new BadRequestException("Invalid classId: " + request.getClassId()));

        int currentCount = enrollmentRepository.countByClassEntityIdAndStatus(classEntity.getId(), "ACTIVE");
        if (currentCount >= classEntity.getCapacity()) {
            throw new BadRequestException("Class is at full capacity");
        }

        Enrollment enrollment = Enrollment.builder().student(student).classEntity(classEntity).status("ACTIVE").build();
        Enrollment saved = enrollmentRepository.save(enrollment);
        log.info("Student id={} enrolled in class id={}", student.getId(), classEntity.getId());
        return toResponse(saved);
    }

    @Override
    public List<EnrollmentResponse> getByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<EnrollmentResponse> getByClass(Long classId) {
        return enrollmentRepository.findByClassEntityId(classId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public EnrollmentResponse updateStatus(Long id, String status) {
        if (!VALID_STATUSES.contains(status)) {
            throw new BadRequestException("Invalid status. Must be one of: " + VALID_STATUSES);
        }
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
        enrollment.setStatus(status);
        Enrollment updated = enrollmentRepository.save(enrollment);
        log.info("Enrollment id={} status changed to {}", id, status);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void drop(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
        enrollment.setStatus("DROPPED");
        enrollmentRepository.save(enrollment);
        log.info("Enrollment id={} dropped", id);
    }

    private EnrollmentResponse toResponse(Enrollment e) {
        return EnrollmentResponse.builder()
                .id(e.getId())
                .studentId(e.getStudent().getId())
                .studentName(e.getStudent().getFullName())
                .classId(e.getClassEntity().getId())
                .className(e.getClassEntity().getName())
                .enrolledDate(e.getEnrolledDate())
                .status(e.getStatus())
                .build();
    }
}

