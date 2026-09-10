package lk.SmartClass.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AiChatRequest {
    @NotBlank(message = "message must not be blank")
    @Size(max = 1000, message = "message must be at most 1000 characters")
    private String message;
}
