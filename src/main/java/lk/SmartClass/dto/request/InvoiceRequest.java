package lk.SmartClass.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InvoiceRequest {
    @NotNull(message = "Student ID is required") private Long studentId;
    @NotNull(message = "Fee structure ID is required") private Long feeStructureId;
    @NotNull(message = "Due date is required") private LocalDate dueDate;
}

