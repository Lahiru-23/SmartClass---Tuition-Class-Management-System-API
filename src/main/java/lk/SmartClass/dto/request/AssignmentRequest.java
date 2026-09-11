package lk.SmartClass.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;


@Data
public class AssignmentRequest {
    @NotNull(message = "Class ID is required") private Long classId;
    @NotBlank(message = "Title is required") private String title;
    private String description;
    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date cannot be in the past")
    private LocalDate dueDate;
    @Min(value = 1, message = "Max marks must be at least 1") private Integer maxMarks;
}
