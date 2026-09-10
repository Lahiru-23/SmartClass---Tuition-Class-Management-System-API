package lk.SmartClass.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionRequest {
    @NotNull(message = "Assignment ID is required") private Long assignmentId;
    @NotNull(message = "Student ID is required") private Long studentId;
    private String fileUrl;
}
