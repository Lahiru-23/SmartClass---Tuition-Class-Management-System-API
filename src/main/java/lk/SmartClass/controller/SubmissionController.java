package lk.SmartClass.controller;

import jakarta.validation.Valid;
import lk.SmartClass.dto.request.GradeSubmissionRequest;
import lk.SmartClass.dto.request.SubmissionRequest;
import lk.SmartClass.dto.response.SubmissionResponse;
import lk.SmartClass.security.AccessGuard;
import lk.SmartClass.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;
    private final AccessGuard accessGuard;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    public ResponseEntity<SubmissionResponse> submit(@Valid @RequestBody SubmissionRequest request, Authentication authentication) {
        // A STUDENT may only submit as themself; without this a student could submit work
        // under any other student's id just by putting a different studentId in the body.
        accessGuard.requireSelfStudentOrStaff(request.getStudentId(), authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(submissionService.submit(request));
    }

    @PatchMapping("/{id}/grade")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<SubmissionResponse> grade(@PathVariable Long id, @Valid @RequestBody GradeSubmissionRequest request) {
        return ResponseEntity.ok(submissionService.grade(id, request));
    }

    @GetMapping("/assignment/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<List<SubmissionResponse>> byAssignment(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(submissionService.getByAssignment(assignmentId));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SubmissionResponse>> byStudent(@PathVariable Long studentId, Authentication authentication) {
        accessGuard.requireSelfStudentOrStaff(studentId, authentication);
        return ResponseEntity.ok(submissionService.getByStudent(studentId));
    }
}

