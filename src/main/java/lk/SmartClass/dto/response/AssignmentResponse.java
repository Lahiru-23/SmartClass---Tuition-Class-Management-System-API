package lk.SmartClass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;


@Data
@Builder
@AllArgsConstructor
public class AssignmentResponse {
    private Long id;
    private Long classId;
    private String className;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Integer maxMarks;
}
