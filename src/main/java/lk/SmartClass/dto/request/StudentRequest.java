package lk.SmartClass.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;
    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;
    private Long guardianId;

    private Long userId;
}

