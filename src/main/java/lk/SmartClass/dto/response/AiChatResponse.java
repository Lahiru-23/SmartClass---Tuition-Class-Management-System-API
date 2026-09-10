package lk.SmartClass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AiChatResponse {
    private String reply;
    /** "local" (built-in assistant) or "llm" (external provider answered this one). */
    private String source;
}

