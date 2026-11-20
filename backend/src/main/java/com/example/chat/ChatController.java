package com.example.chat;

import com.example.chat.dto.ChatRequest;
import com.example.chat.dto.ChatResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1")
public class ChatController {
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final ModelService modelService;

    public ChatController(ModelService modelService) {
        this.modelService = modelService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(
            @Valid @RequestBody ChatRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    new ChatResponse(UUID.randomUUID().toString(), "", 0, "error"));
        }

        String sessionId = request.getSessionId() == null || request.getSessionId().isBlank()
                ? UUID.randomUUID().toString()
                : request.getSessionId();
        long start = Instant.now().toEpochMilli();
        String answer = modelService.generateResponse(request.getQuestion(), sessionId);
        long elapsed = Instant.now().toEpochMilli() - start;
        String status = answer == null || answer.isBlank() ? "error" : "ok";

        log.info("Chat finalizado. sessionId={} elapsedMs={} status={}", sessionId, elapsed, status);
        ChatResponse response = new ChatResponse(
                UUID.randomUUID().toString(), answer == null ? "" : answer, elapsed, status);
        return "ok".equals(status)
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(502).body(response);
    }
}

