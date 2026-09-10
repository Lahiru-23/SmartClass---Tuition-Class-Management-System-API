package lk.SmartClass.service.impl;

import lk.SmartClass.dto.request.InvoiceRequest;
import lk.SmartClass.dto.response.InvoiceResponse;
import lk.SmartClass.entity.FeeStructure;
import lk.SmartClass.entity.Invoice;
import lk.SmartClass.entity.Student;
import lk.SmartClass.exception.BadRequestException;
import lk.SmartClass.exception.ResourceNotFoundException;
import lk.SmartClass.repository.FeeStructureRepository;
import lk.SmartClass.repository.InvoiceRepository;
import lk.SmartClass.repository.PaymentRepository;
import lk.SmartClass.repository.StudentRepository;
import lk.SmartClass.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);

    private final InvoiceRepository invoiceRepository;
    private final StudentRepository studentRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public InvoiceResponse create(InvoiceRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new BadRequestException("Invalid studentId: " + request.getStudentId()));
        FeeStructure feeStructure = feeStructureRepository.findById(request.getFeeStructureId())
                .orElseThrow(() -> new BadRequestException("Invalid feeStructureId: " + request.getFeeStructureId()));

        Invoice invoice = Invoice.builder()
                .student(student).feeStructure(feeStructure)
                .amountDue(feeStructure.getAmount())
                .dueDate(request.getDueDate())
                .status("PENDING")
                .build();

        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice created: id={}, student={}, amount={}", saved.getId(), student.getId(), saved.getAmountDue());
        return toResponse(saved);
    }

    @Override
    public InvoiceResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public List<InvoiceResponse> getByStudent(Long studentId) {
        return invoiceRepository.findByStudentId(studentId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<InvoiceResponse> getOverdue() {
        List<Invoice> pending = invoiceRepository.findByStatus("PENDING");
        LocalDate today = LocalDate.now();
        pending.stream().filter(inv -> inv.getDueDate().isBefore(today)).forEach(inv -> {
            inv.setStatus("OVERDUE");
            invoiceRepository.save(inv);
        });
        return invoiceRepository.findByStatus("OVERDUE").stream().map(this::toResponse).toList();
    }

    private Invoice findOrThrow(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }

    private InvoiceResponse toResponse(Invoice inv) {
        BigDecimal paid = paymentRepository.findByInvoiceId(inv.getId()).stream()
                .map(p -> p.getAmountPaid()).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal balance = inv.getAmountDue().subtract(paid);

        return InvoiceResponse.builder()
                .id(inv.getId())
                .studentId(inv.getStudent().getId())
                .studentName(inv.getStudent().getFullName())
                .amountDue(inv.getAmountDue())
                .amountPaid(paid)
                .balance(balance)
                .dueDate(inv.getDueDate())
                .status(inv.getStatus())
                .build();
    }
}
