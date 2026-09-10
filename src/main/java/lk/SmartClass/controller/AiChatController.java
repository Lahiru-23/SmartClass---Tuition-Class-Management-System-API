package lk.SmartClass.controller;

import jakarta.validation.Valid;
import lk.SmartClass.dto.request.AiChatRequest;
import lk.SmartClass.dto.response.AiChatResponse;
import lk.SmartClass.service.AiChatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiChatController {

    private static final Logger log = LoggerFactory.getLogger(AiChatController.class);
    private final AiChatService aiChatService;

    @PostMapping("/chat")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AiChatResponse> chat(@Valid @RequestBody AiChatRequest request, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "guest";
        List<String> roles = authentication == null ? List.of() :
                authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        log.info("AI chat message from '{}': {}", username, request.getMessage());
        return ResponseEntity.ok(aiChatService.chat(request.getMessage(), username, roles));
    }
}