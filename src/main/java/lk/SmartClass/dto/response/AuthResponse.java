package lk.SmartClass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private List<String> roles;
    private long expiresInMs;
    private Long userId;
    /** Populated only when this account is linked to a Student profile. */
    private Long studentId;
    /** Populated only when this account is linked to a Teacher profile. */
    private Long teacherId;
}