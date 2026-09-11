package lk.SmartClass.service.impl;

import lk.SmartClass.dto.request.StudentRequest;
import lk.SmartClass.dto.response.StudentResponse;
import lk.SmartClass.entity.Guardian;
import lk.SmartClass.entity.Student;
import lk.SmartClass.entity.User;
import lk.SmartClass.exception.BadRequestException;
import lk.SmartClass.exception.ResourceNotFoundException;
import lk.SmartClass.repository.GuardianRepository;
import lk.SmartClass.repository.StudentRepository;
import lk.SmartClass.repository.UserRepository;
import lk.SmartClass.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;
    private final GuardianRepository guardianRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public StudentResponse create(StudentRequest request) {
        Guardian guardian = resolveGuardian(request.getGuardianId());
        User user = resolveUser(request.getUserId(), null);
        Student student = Student.builder()
                .fullName(request.getFullName())
                .dob(request.getDob())
                .guardian(guardian)
                .user(user)
                .build();
        Student saved = studentRepository.save(student);
        log.info("Student created: id={}, name='{}'", saved.getId(), saved.getFullName());
        return toResponse(saved);
    }

    @Override
    public StudentResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public List<StudentResponse> getAll() {
        return studentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = findOrThrow(id);
        student.setFullName(request.getFullName());
        student.setDob(request.getDob());
        student.setGuardian(resolveGuardian(request.getGuardianId()));
        student.setUser(resolveUser(request.getUserId(), id));
        Student updated = studentRepository.save(student);
        log.info("Student updated: id={}", updated.getId());
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Student student = findOrThrow(id);
        studentRepository.delete(student);
        log.info("Student deleted: id={}", id);
    }

    private Student findOrThrow(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    private Guardian resolveGuardian(Long guardianId) {
        if (guardianId == null) return null;
        return guardianRepository.findById(guardianId)
                .orElseThrow(() -> new BadRequestException("Invalid guardianId: " + guardianId));
    }

    /**
     * Resolves the login account to link this student profile to.
     * {@code currentStudentId} is the id being updated (null when creating), so a student
     * can keep its own existing link without tripping the "already linked" check.
     */
    private User resolveUser(Long userId, Long currentStudentId) {
        if (userId == null) return null;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Invalid userId: " + userId));
        studentRepository.findByUserId(userId).ifPresent(existing -> {
            if (currentStudentId == null || !existing.getId().equals(currentStudentId)) {
                throw new BadRequestException("userId " + userId + " is already linked to another student");
            }
        });
        return user;
    }

    private StudentResponse toResponse(Student s) {
        return StudentResponse.builder()
                .id(s.getId())
                .fullName(s.getFullName())
                .dob(s.getDob())
                .enrollmentDate(s.getEnrollmentDate())
                .guardianId(s.getGuardian() != null ? s.getGuardian().getId() : null)
                .guardianName(s.getGuardian() != null ? s.getGuardian().getFullName() : null)
                .userId(s.getUser() != null ? s.getUser().getId() : null)
                .build();
    }
}
