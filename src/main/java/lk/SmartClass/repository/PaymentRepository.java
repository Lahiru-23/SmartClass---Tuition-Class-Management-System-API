package lk.SmartClass.repository;

import lk.SmartClass.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByInvoiceId(Long invoiceId);
    boolean existsByReceiptNo(String receiptNo);
}
