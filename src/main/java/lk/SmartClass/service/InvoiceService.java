package lk.SmartClass.service;

import lk.SmartClass.dto.request.InvoiceRequest;
import lk.SmartClass.dto.response.InvoiceResponse;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse create(InvoiceRequest request);
    InvoiceResponse getById(Long id);
    List<InvoiceResponse> getByStudent(Long studentId);
    List<InvoiceResponse> getOverdue();
}
