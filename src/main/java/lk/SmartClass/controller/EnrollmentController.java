package lk.SmartClass.controller;

import jakarta.validation.Valid;
import lk.SmartClass.dto.request.EnrollmentRequest;
import lk.SmartClass.dto.response.EnrollmentResponse;
import lk.SmartClass.security.AccessGuard;
import lk.SmartClass.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final AccessGuard accessGuard;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<EnrollmentResponse> enroll(@Valid @RequestBody EnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.enroll(request));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EnrollmentResponse>> byStudent(@PathVariable Long studentId, Authentication authentication) {
        accessGuard.requireSelfStudentOrStaff(studentId, authentication);
        return ResponseEntity.ok(enrollmentService.getByStudent(studentId));
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<List<EnrollmentResponse>> byClass(@PathVariable Long classId) {
        return ResponseEntity.ok(enrollmentService.getByClass(classId));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<EnrollmentResponse> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(enrollmentService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<Void> drop(@PathVariable Long id) {
        enrollmentService.drop(id);
        return ResponseEntity.noContent().build();
    }
}

