package lk.SmartClass.controller;

import jakarta.validation.Valid;
import lk.SmartClass.dto.request.FeeStructureRequest;
import lk.SmartClass.entity.ClassEntity;
import lk.SmartClass.entity.FeeStructure;
import lk.SmartClass.exception.BadRequestException;
import lk.SmartClass.repository.ClassRepository;
import lk.SmartClass.repository.FeeStructureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fee-structures")
@RequiredArgsConstructor
public class FeeStructureController {

    private final FeeStructureRepository feeStructureRepository;
    private final ClassRepository classRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeeStructure> create(@Valid @RequestBody FeeStructureRequest request) {
        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new BadRequestException("Invalid classId: " + request.getClassId()));
        FeeStructure fs = FeeStructure.builder()
                .classEntity(classEntity)
                .amount(request.getAmount())
                .billingCycle(request.getBillingCycle() != null ? request.getBillingCycle() : "MONTHLY")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(feeStructureRepository.save(fs));
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FeeStructure>> byClass(@PathVariable Long classId) {
        return ResponseEntity.ok(feeStructureRepository.findByClassEntityId(classId));
    }
}
