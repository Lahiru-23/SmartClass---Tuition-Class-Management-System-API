package lk.SmartClass.service.impl;

import lk.SmartClass.dto.request.PaymentRequest;
import lk.SmartClass.dto.response.PaymentResponse;
import lk.SmartClass.entity.Invoice;
import lk.SmartClass.entity.Payment;
import lk.SmartClass.exception.BadRequestException;
import lk.SmartClass.repository.InvoiceRepository;
import lk.SmartClass.repository.PaymentRepository;
import lk.SmartClass.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional
    public PaymentResponse recordPayment(PaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new BadRequestException("Invalid invoiceId: " + request.getInvoiceId()));

        if ("PAID".equals(invoice.getStatus())) {
            throw new BadRequestException("Invoice is already fully paid");
        }

        BigDecimal alreadyPaid = paymentRepository.findByInvoiceId(invoice.getId()).stream()
                .map(Payment::getAmountPaid).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remaining = invoice.getAmountDue().subtract(alreadyPaid);

        if (request.getAmountPaid().compareTo(remaining) > 0) {
            throw new BadRequestException(
                    "Payment amount (" + request.getAmountPaid() + ") exceeds remaining balance (" + remaining + ")");
        }

        Payment payment = Payment.builder()
                .invoice(invoice).amountPaid(request.getAmountPaid())
                .method(request.getMethod()).receiptNo(generateReceiptNo()).build();

        Payment saved = paymentRepository.save(payment);

        BigDecimal newTotalPaid = alreadyPaid.add(request.getAmountPaid());
        if (newTotalPaid.compareTo(invoice.getAmountDue()) >= 0) {
            invoice.setStatus("PAID");
            invoiceRepository.save(invoice);
            log.info("Invoice id={} fully paid", invoice.getId());
        }

        log.info("Payment recorded: invoice={}, amount={}, receipt={}",
                invoice.getId(), request.getAmountPaid(), saved.getReceiptNo());

        return PaymentResponse.builder()
                .id(saved.getId()).invoiceId(invoice.getId())
                .amountPaid(saved.getAmountPaid()).paymentDate(saved.getPaymentDate())
                .method(saved.getMethod()).receiptNo(saved.getReceiptNo())
                .invoiceStatusAfterPayment(invoice.getStatus()).build();
    }

    @Override
    public List<PaymentResponse> getByInvoice(Long invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId).stream()
                .map(p -> PaymentResponse.builder()
                        .id(p.getId()).invoiceId(p.getInvoice().getId())
                        .amountPaid(p.getAmountPaid()).paymentDate(p.getPaymentDate())
                        .method(p.getMethod()).receiptNo(p.getReceiptNo())
                        .invoiceStatusAfterPayment(p.getInvoice().getStatus()).build())
                .toList();
    }

    private String generateReceiptNo() {
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "RCP-" + stamp + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
