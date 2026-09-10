package lk.SmartClass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class AttendanceResponse {
    private Long id;
    private Long enrollmentId;
    private String studentName;
    private LocalDate date;
    private String status;
    private String markedByName;
}
