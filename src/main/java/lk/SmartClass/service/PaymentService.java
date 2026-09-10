package lk.SmartClass.service;

import lk.SmartClass.dto.request.PaymentRequest;
import lk.SmartClass.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse recordPayment(PaymentRequest request);
    List<PaymentResponse> getByInvoice(Long invoiceId);
}
