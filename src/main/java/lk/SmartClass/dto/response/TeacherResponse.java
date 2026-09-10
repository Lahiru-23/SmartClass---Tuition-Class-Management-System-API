package lk.SmartClass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class TeacherResponse {
    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private String phone;
    private String specialization;
    private LocalDate hireDate;
}