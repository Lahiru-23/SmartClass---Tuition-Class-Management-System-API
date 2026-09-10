package lk.SmartClass.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnnouncementRequest {
    private Long classId;
    @NotBlank(message = "Title is required") private String title;
    @NotBlank(message = "Body is required") private String body;
}
