package lk.SmartClass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ClassResponse {
    private Long id;
    private String subjectName;
    private String teacherName;
    private String termName;
    private String name;
    private Integer capacity;
    private String mode;
    private String room;
    private int enrolledCount;
}
