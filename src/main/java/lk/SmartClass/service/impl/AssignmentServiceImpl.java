package lk.SmartClass.service.impl;


import lk.SmartClass.dto.request.AssignmentRequest;
import lk.SmartClass.dto.response.AssignmentResponse;
import lk.SmartClass.entity.Assignment;
import lk.SmartClass.entity.ClassEntity;
import lk.SmartClass.exception.BadRequestException;
import lk.SmartClass.exception.ResourceNotFoundException;
import lk.SmartClass.repository.AssignmentRepository;
import lk.SmartClass.repository.ClassRepository;
import lk.SmartClass.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private static final Logger log = LoggerFactory.getLogger(AssignmentServiceImpl.class);

    private final AssignmentRepository assignmentRepository;
    private final ClassRepository classRepository;

    @Override
    @Transactional
    public AssignmentResponse create(AssignmentRequest request) {
        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new BadRequestException("Invalid classId: " + request.getClassId()));

        Assignment assignment = Assignment.builder()
                .classEntity(classEntity)
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .maxMarks(request.getMaxMarks() != null ? request.getMaxMarks() : 100)
                .build();

        Assignment saved = assignmentRepository.save(assignment);
        log.info("Assignment created: id={}, title='{}', class={}", saved.getId(), saved.getTitle(), classEntity.getId());
        return toResponse(saved);
    }

    @Override
    public AssignmentResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public List<AssignmentResponse> getByClass(Long classId) {
        return assignmentRepository.findByClassEntityId(classId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public AssignmentResponse update(Long id, AssignmentRequest request) {
        Assignment assignment = findOrThrow(id);
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDueDate(request.getDueDate());
        if (request.getMaxMarks() != null) assignment.setMaxMarks(request.getMaxMarks());
        Assignment updated = assignmentRepository.save(assignment);
        log.info("Assignment updated: id={}", updated.getId());
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Assignment assignment = findOrThrow(id);
        assignmentRepository.delete(assignment);
        log.info("Assignment deleted: id={}", id);
    }

    private Assignment findOrThrow(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));
    }

    private AssignmentResponse toResponse(Assignment a) {
        return AssignmentResponse.builder()
                .id(a.getId())
                .classId(a.getClassEntity().getId())
                .className(a.getClassEntity().getName())
                .title(a.getTitle())
                .description(a.getDescription())
                .dueDate(a.getDueDate())
                .maxMarks(a.getMaxMarks())
                .build();
    }
}
