package lk.SmartClass.repository;

import lk.SmartClass.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByStudentId(Long studentId);
    List<Invoice> findByStatus(String status);
}