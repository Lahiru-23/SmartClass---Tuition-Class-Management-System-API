package lk.SmartClass.service;

import lk.SmartClass.dto.response.AiChatResponse;

import java.util.List;

public interface AiChatService {
    AiChatResponse chat(String message, String username, List<String> roles);
}
