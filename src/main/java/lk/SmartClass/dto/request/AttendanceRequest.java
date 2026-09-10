package lk.SmartClass.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AttendanceRequest {
    @NotNull(message = "Enrollment ID is required") private Long enrollmentId;
    @NotNull(message = "Date is required") private LocalDate date;
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "PRESENT|ABSENT|LATE", message = "Status must be PRESENT, ABSENT, or LATE")
    private String status;
}
