package com.example.chat.impl;

import com.example.chat.ModelService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SpringAiModelService implements ModelService {
    private final ChatClient chatClient;

    public SpringAiModelService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("Responde en español, de forma clara y concisa. No inventes datos.")
                .build();
    }

    @Override
    public String generateResponse(String question, String sessionId) {
        String answer = chatClient.prompt().user(question).call().content();
        return answer == null ? "" : answer;
    }
}

