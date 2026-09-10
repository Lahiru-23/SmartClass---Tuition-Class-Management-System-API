package lk.SmartClass.controller;

import jakarta.validation.Valid;
import lk.SmartClass.dto.request.InvoiceRequest;
import lk.SmartClass.dto.response.InvoiceResponse;
import lk.SmartClass.security.AccessGuard;
import lk.SmartClass.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final AccessGuard accessGuard;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceResponse> create(@Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InvoiceResponse> getById(@PathVariable Long id, Authentication authentication) {
        InvoiceResponse invoice = invoiceService.getById(id);
        accessGuard.requireSelfStudentOrStaff(invoice.getStudentId(), authentication);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<InvoiceResponse>> byStudent(@PathVariable Long studentId, Authentication authentication) {
        accessGuard.requireSelfStudentOrStaff(studentId, authentication);
        return ResponseEntity.ok(invoiceService.getByStudent(studentId));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InvoiceResponse>> overdue() {
        return ResponseEntity.ok(invoiceService.getOverdue());
    }
}
