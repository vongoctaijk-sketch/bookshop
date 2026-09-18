package com.example.Bookshop.controller;

import com.example.Bookshop.dto.AiRequest;
import com.example.Bookshop.service.GeminiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final GeminiService geminiService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("status", "ok");
        payload.put("service", "gemini");
        return ResponseEntity.ok(payload);
    }

    @PostMapping({"/chat", "/generate"})
    public ResponseEntity<Map<String, Object>> generate(@Valid @RequestBody AiRequest request) {
        String prompt = request.prompt();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("prompt", prompt);
        response.put("answer", geminiService.generateText(prompt));
        return ResponseEntity.ok(response);
    }
}
