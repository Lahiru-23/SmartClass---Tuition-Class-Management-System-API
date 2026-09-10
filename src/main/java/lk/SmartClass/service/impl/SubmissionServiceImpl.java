package lk.SmartClass.service.impl;

import lk.SmartClass.dto.request.GradeSubmissionRequest;
import lk.SmartClass.dto.request.SubmissionRequest;
import lk.SmartClass.dto.response.SubmissionResponse;
import lk.SmartClass.entity.Assignment;
import lk.SmartClass.entity.AssignmentSubmission;
import lk.SmartClass.entity.Student;
import lk.SmartClass.exception.BadRequestException;
import lk.SmartClass.exception.ResourceNotFoundException;
import lk.SmartClass.repository.AssignmentRepository;
import lk.SmartClass.repository.AssignmentSubmissionRepository;
import lk.SmartClass.repository.StudentRepository;
import lk.SmartClass.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionServiceImpl.class);

    private final AssignmentSubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public SubmissionResponse submit(SubmissionRequest request) {
        if (submissionRepository.existsByAssignmentIdAndStudentId(request.getAssignmentId(), request.getStudentId())) {
            throw new BadRequestException("Student has already submitted this assignment");
        }
        Assignment assignment = assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new BadRequestException("Invalid assignmentId: " + request.getAssignmentId()));
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new BadRequestException("Invalid studentId: " + request.getStudentId()));

        AssignmentSubmission submission = AssignmentSubmission.builder()
                .assignment(assignment).student(student).fileUrl(request.getFileUrl()).build();

        AssignmentSubmission saved = submissionRepository.save(submission);
        log.info("Submission created: assignment={}, student={}", assignment.getId(), student.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public SubmissionResponse grade(Long id, GradeSubmissionRequest request) {
        AssignmentSubmission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + id));

        int maxMarks = submission.getAssignment().getMaxMarks();
        if (request.getMarksObtained() > maxMarks) {
            throw new BadRequestException("Marks cannot exceed max marks (" + maxMarks + ")");
        }

        submission.setMarksObtained(request.getMarksObtained());
        submission.setFeedback(request.getFeedback());

        AssignmentSubmission updated = submissionRepository.save(submission);
        log.info("Submission graded: id={}, marks={}", id, request.getMarksObtained());
        return toResponse(updated);
    }

    @Override
    public List<SubmissionResponse> getByAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<SubmissionResponse> getByStudent(Long studentId) {
        return submissionRepository.findByStudentId(studentId).stream().map(this::toResponse).toList();
    }

    private SubmissionResponse toResponse(AssignmentSubmission s) {
        boolean late = s.getSubmittedAt().toLocalDate().isAfter(s.getAssignment().getDueDate());
        return SubmissionResponse.builder()
                .id(s.getId())
                .assignmentId(s.getAssignment().getId())
                .assignmentTitle(s.getAssignment().getTitle())
                .studentId(s.getStudent().getId())
                .studentName(s.getStudent().getFullName())
                .submittedAt(s.getSubmittedAt())
                .fileUrl(s.getFileUrl())
                .marksObtained(s.getMarksObtained())
                .maxMarks(s.getAssignment().getMaxMarks())
                .feedback(s.getFeedback())
                .late(late)
                .build();
    }
}

