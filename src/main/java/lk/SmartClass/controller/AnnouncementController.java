package lk.SmartClass.controller;


import jakarta.validation.Valid;
import lk.SmartClass.dto.request.AnnouncementRequest;
import lk.SmartClass.dto.response.AnnouncementResponse;
import lk.SmartClass.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<AnnouncementResponse> post(@Valid @RequestBody AnnouncementRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(announcementService.post(request, authentication.getName()));
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AnnouncementResponse>> byClass(@PathVariable Long classId) {
        return ResponseEntity.ok(announcementService.getByClass(classId));
    }

    @GetMapping("/institute-wide")
    public ResponseEntity<List<AnnouncementResponse>> instituteWide() {
        return ResponseEntity.ok(announcementService.getInstituteWide());
    }
}
