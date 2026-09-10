package lk.SmartClass.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FeeStructureRequest {
    @NotNull(message = "Class ID is required") private Long classId;
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    @Pattern(regexp = "MONTHLY|TERM", message = "Billing cycle must be MONTHLY or TERM")
    private String billingCycle;
}