package lk.SmartClass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class StudentResponse {
    private Long id;
    private String fullName;
    private LocalDate dob;
    private LocalDate enrollmentDate;
    private Long guardianId;
    private String guardianName;
    private Long userId;
}
