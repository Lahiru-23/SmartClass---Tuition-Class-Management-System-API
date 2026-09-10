package lk.SmartClass.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClassRequest {
    @NotNull(message = "Subject ID is required") private Long subjectId;
    @NotNull(message = "Teacher ID is required") private Long teacherId;
    @NotNull(message = "Term ID is required") private Long termId;
    @NotBlank(message = "Class name is required") private String name;
    @Min(value = 1, message = "Capacity must be at least 1") private Integer capacity;
    private String mode;
    private String room;
}
