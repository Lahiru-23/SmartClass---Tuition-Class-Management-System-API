package lk.SmartClass.controller;

import jakarta.validation.Valid;
import lk.SmartClass.dto.request.AttendanceRequest;
import lk.SmartClass.dto.response.AttendanceResponse;
import lk.SmartClass.security.AccessGuard;
import lk.SmartClass.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final AccessGuard accessGuard;

    // Only TEACHER, not ADMIN: attendance_records.marked_by is a NOT NULL FK to the teachers
    // table, and an ADMIN account has no linked Teacher row, so letting ADMIN call this used
    // to fail with a 400 ("Marking teacher not found") despite being "authorized".
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AttendanceResponse> mark(@Valid @RequestBody AttendanceRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.mark(request, authentication.getName()));
    }

    @GetMapping("/enrollment/{enrollmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AttendanceResponse>> byEnrollment(@PathVariable Long enrollmentId, Authentication authentication) {
        accessGuard.requireSelfEnrollmentOrStaff(enrollmentId, authentication);
        return ResponseEntity.ok(attendanceService.getByEnrollment(enrollmentId));
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<List<AttendanceResponse>> byClassAndDate(
            @PathVariable Long classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getByClassAndDate(classId, date));
    }
}
