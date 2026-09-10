package lk.SmartClass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GuardianResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String address;
    private Long userId;
}