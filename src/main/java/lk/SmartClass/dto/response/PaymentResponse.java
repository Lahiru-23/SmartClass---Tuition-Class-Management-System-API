package lk.SmartClass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long invoiceId;
    private BigDecimal amountPaid;
    private LocalDateTime paymentDate;
    private String method;
    private String receiptNo;
    private String invoiceStatusAfterPayment;
}
