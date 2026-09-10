package lk.SmartClass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class AnnouncementResponse {
    private Long id;
    private Long classId;
    private String className;
    private String postedByUsername;
    private String title;
    private String body;
    private LocalDateTime postedAt;
}
