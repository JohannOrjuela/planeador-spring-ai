package com.example.chat.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/health")
public class LlmHealthController {
    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public LlmHealthController(
            @Value("${spring.ai.ollama.base-url:http://localhost:11434}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @GetMapping("/llm")
    public ResponseEntity<?> health() {
        try {
            String endpoint = baseUrl.endsWith("/") ? baseUrl + "api/tags" : baseUrl + "/api/tags";
            Map<?, ?> result = restTemplate.getForObject(endpoint, Map.class);
            return ResponseEntity.ok(Map.of("ok", true, "result", result == null ? Map.of() : result));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(502).body(Map.of("ok", false, "error", ex.getMessage()));
        }
    }
}

