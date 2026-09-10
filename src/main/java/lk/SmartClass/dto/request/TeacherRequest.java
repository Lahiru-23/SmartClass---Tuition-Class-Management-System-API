package lk.SmartClass.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TeacherRequest {
    @NotNull(message = "User ID is required")
    private Long userId;
    @NotBlank(message = "Full name is required")
    private String fullName;
    private String phone;
    private String specialization;
    private LocalDate hireDate;
}