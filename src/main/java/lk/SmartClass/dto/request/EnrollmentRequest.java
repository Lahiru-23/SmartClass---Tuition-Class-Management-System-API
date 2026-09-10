package lk.SmartClass.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollmentRequest {
    @NotNull(message = "Student ID is required") private Long studentId;
    @NotNull(message = "Class ID is required") private Long classId;
}
