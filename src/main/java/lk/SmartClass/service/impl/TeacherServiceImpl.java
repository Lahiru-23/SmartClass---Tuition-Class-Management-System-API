package lk.SmartClass.service.impl;

import lk.SmartClass.dto.request.TeacherRequest;
import lk.SmartClass.dto.response.TeacherResponse;
import lk.SmartClass.entity.Teacher;
import lk.SmartClass.entity.User;
import lk.SmartClass.exception.BadRequestException;
import lk.SmartClass.exception.ResourceNotFoundException;
import lk.SmartClass.repository.ClassRepository;
import lk.SmartClass.repository.TeacherRepository;
import lk.SmartClass.repository.UserRepository;
import lk.SmartClass.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private static final Logger log = LoggerFactory.getLogger(TeacherServiceImpl.class);

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final ClassRepository classRepository;

    @Override
    @Transactional
    public TeacherResponse create(TeacherRequest request) {
        User user = resolveUser(request.getUserId(), null);
        Teacher teacher = Teacher.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .specialization(request.getSpecialization())
                .hireDate(request.getHireDate())
                .build();
        Teacher saved = teacherRepository.save(teacher);
        log.info("Teacher created: id={}, name='{}'", saved.getId(), saved.getFullName());
        return toResponse(saved);
    }

    @Override
    public TeacherResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public List<TeacherResponse> getAll() {
        return teacherRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public TeacherResponse update(Long id, TeacherRequest request) {
        Teacher teacher = findOrThrow(id);
        teacher.setUser(resolveUser(request.getUserId(), id));
        teacher.setFullName(request.getFullName());
        teacher.setPhone(request.getPhone());
        teacher.setSpecialization(request.getSpecialization());
        teacher.setHireDate(request.getHireDate());
        Teacher updated = teacherRepository.save(teacher);
        log.info("Teacher updated: id={}", updated.getId());
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Teacher teacher = findOrThrow(id);
        if (!classRepository.findByTeacherId(id).isEmpty()) {
            throw new BadRequestException("Cannot delete teacher: still assigned to one or more classes");
        }
        teacherRepository.delete(teacher);
        log.info("Teacher deleted: id={}", id);
    }

    private Teacher findOrThrow(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
    }

    private User resolveUser(Long userId, Long currentTeacherId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Invalid userId: " + userId));
        teacherRepository.findByUserId(userId).ifPresent(existing -> {
            if (currentTeacherId == null || !existing.getId().equals(currentTeacherId)) {
                throw new BadRequestException("userId " + userId + " is already linked to another teacher");
            }
        });
        return user;
    }

    private TeacherResponse toResponse(Teacher t) {
        return TeacherResponse.builder()
                .id(t.getId())
                .userId(t.getUser().getId())
                .username(t.getUser().getUsername())
                .fullName(t.getFullName())
                .phone(t.getPhone())
                .specialization(t.getSpecialization())
                .hireDate(t.getHireDate())
                .build();
    }
}

