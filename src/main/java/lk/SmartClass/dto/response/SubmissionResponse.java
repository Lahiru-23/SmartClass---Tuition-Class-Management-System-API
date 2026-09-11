package lk.SmartClass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;

@Data
@Builder
@AllArgsConstructor
public class SubmissionResponse {
    private Long id;
    private Long assignmentId;
    private String assignmentTitle;
    private Long studentId;
    private String studentName;
    private ChronoLocalDate submittedAt;
    private String fileUrl;
    private Integer marksObtained;
    private Integer maxMarks;
    private String feedback;
    private boolean late;
}

