package lk.SmartClass.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GuardianRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;
    @NotBlank(message = "Phone is required")
    private String phone;
    private String address;
    private Long userId;
}
