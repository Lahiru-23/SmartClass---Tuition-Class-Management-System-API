package lk.SmartClass.controller;

import jakarta.validation.Valid;
import lk.SmartClass.dto.request.PaymentRequest;
import lk.SmartClass.dto.response.PaymentResponse;
import lk.SmartClass.security.AccessGuard;
import lk.SmartClass.service.InvoiceService;
import lk.SmartClass.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final InvoiceService invoiceService;
    private final AccessGuard accessGuard;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> record(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.recordPayment(request));
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PaymentResponse>> byInvoice(@PathVariable Long invoiceId, Authentication authentication) {
        Long studentId = invoiceService.getById(invoiceId).getStudentId();
        accessGuard.requireSelfStudentOrStaff(studentId, authentication);
        return ResponseEntity.ok(paymentService.getByInvoice(invoiceId));
    }
}
