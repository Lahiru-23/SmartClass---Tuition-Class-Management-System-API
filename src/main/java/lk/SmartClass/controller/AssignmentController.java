package lk.SmartClass.controller;

import jakarta.validation.Valid;
import lk.SmartClass.dto.request.AssignmentRequest;
import lk.SmartClass.dto.response.AssignmentResponse;
import lk.SmartClass.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<AssignmentResponse> create(@Valid @RequestBody AssignmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AssignmentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.getById(id));
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AssignmentResponse>> byClass(@PathVariable Long classId) {
        return ResponseEntity.ok(assignmentService.getByClass(classId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<AssignmentResponse> update(@PathVariable Long id, @Valid @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(assignmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assignmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
