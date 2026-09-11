package lk.SmartClass.service.impl;

import lk.SmartClass.dto.request.ClassRequest;
import lk.SmartClass.dto.response.ClassResponse;
import lk.SmartClass.entity.ClassEntity;
import lk.SmartClass.entity.Subject;
import lk.SmartClass.entity.Teacher;
import lk.SmartClass.entity.Term;
import lk.SmartClass.exception.BadRequestException;
import lk.SmartClass.exception.ResourceNotFoundException;
import lk.SmartClass.repository.*;
import lk.SmartClass.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private static final Logger log = LoggerFactory.getLogger(ClassServiceImpl.class);

    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final TermRepository termRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional
    public ClassResponse create(ClassRequest request) {
        ClassEntity entity = ClassEntity.builder()
                .subject(findSubject(request.getSubjectId()))
                .teacher(findTeacher(request.getTeacherId()))
                .term(findTerm(request.getTermId()))
                .name(request.getName())
                .capacity(request.getCapacity() != null ? request.getCapacity() : 30)
                .mode(request.getMode() != null ? request.getMode() : "PHYSICAL")
                .room(request.getRoom())
                .build();
        ClassEntity saved = classRepository.save(entity);
        log.info("Class created: id={}, name='{}'", saved.getId(), saved.getName());
        return toResponse(saved);
    }

    @Override
    public ClassResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public List<ClassResponse> getAll() {
        return classRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public ClassResponse update(Long id, ClassRequest request) {
        ClassEntity entity = findOrThrow(id);
        entity.setSubject(findSubject(request.getSubjectId()));
        entity.setTeacher(findTeacher(request.getTeacherId()));
        entity.setTerm(findTerm(request.getTermId()));
        entity.setName(request.getName());
        if (request.getCapacity() != null) entity.setCapacity(request.getCapacity());
        if (request.getMode() != null) entity.setMode(request.getMode());
        entity.setRoom(request.getRoom());
        ClassEntity updated = classRepository.save(entity);
        log.info("Class updated: id={}", updated.getId());
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ClassEntity entity = findOrThrow(id);
        classRepository.delete(entity);
        log.info("Class deleted: id={}", id);
    }

    private ClassEntity findOrThrow(Long id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + id));
    }

    private Subject findSubject(Long id) {
        return subjectRepository.findById(id).orElseThrow(() -> new BadRequestException("Invalid subjectId: " + id));
    }

    private Teacher findTeacher(Long id) {
        return teacherRepository.findById(id).orElseThrow(() -> new BadRequestException("Invalid teacherId: " + id));
    }

    private Term findTerm(Long id) {
        return termRepository.findById(id).orElseThrow(() -> new BadRequestException("Invalid termId: " + id));
    }

    private ClassResponse toResponse(ClassEntity c) {
        int enrolledCount = enrollmentRepository.countByClassEntityIdAndStatus(c.getId(), "ACTIVE");
        return ClassResponse.builder()
                .id(c.getId())
                .subjectName(c.getSubject().getName())
                .teacherName(c.getTeacher().getFullName())
                .termName(c.getTerm().getName())
                .name(c.getName())
                .capacity(c.getCapacity())
                .mode(c.getMode())
                .room(c.getRoom())
                .enrolledCount(enrolledCount)
                .build();
    }
}

