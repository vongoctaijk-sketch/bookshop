package com.example.Bookshop.controller;

import com.example.Bookshop.dto.AiRequest;
import com.example.Bookshop.entity.Conversation;
import com.example.Bookshop.entity.ConversationMessage;
import com.example.Bookshop.entity.MessageRole;
import com.example.Bookshop.service.ConversationService;
import com.example.Bookshop.service.GeminiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final GeminiService geminiService;
    private final ConversationService conversationService;

    // =========================
    // Health check
    // =========================

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("status", "ok");
        response.put("service", "gemini");

        return ResponseEntity.ok(response);
    }

    // =========================
    // Tạo conversation mới
    // =========================

    @PostMapping("/conversations")
    public ResponseEntity<Map<String, Object>> createConversation() {

        Conversation conversation =
                conversationService.createConversation();

        return ResponseEntity.ok(
                Map.of(
                        "conversationId",
                        conversation.getId()
                )
        );
    }

    // =========================
    // Lấy lịch sử conversation
    // =========================

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<Map<String, Object>>> getMessages(
            @PathVariable Integer conversationId
    ) {

        List<Map<String, Object>> messages =
                conversationService
                        .getMessages(conversationId)
                        .stream()
                        .map(this::toMessageResponse)
                        .toList();

        return ResponseEntity.ok(messages);
    }

    // =========================
    // Chat trong conversation
    // =========================

    @PostMapping("/conversations/{conversationId}/chat")
    public ResponseEntity<Map<String, Object>> chat(
            @PathVariable Integer conversationId,
            @Valid @RequestBody AiRequest request
    ) {

        String prompt = request.prompt();

        // Kiểm tra conversation có tồn tại
        conversationService.getConversation(conversationId);

        // Lưu câu hỏi của USER
        conversationService.saveMessage(
                conversationId,
                MessageRole.USER,
                prompt
        );

        // Gọi Gemini
        String answer =
                geminiService.generateText(
                        conversationId,
                        prompt
                );

        // Lưu câu trả lời của AI
        conversationService.saveMessage(
                conversationId,
                MessageRole.ASSISTANT,
                answer
        );

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("conversationId", conversationId);
        response.put("prompt", prompt);
        response.put("answer", answer);

        return ResponseEntity.ok(response);
    }

    // =========================
    // Chuyển Message Entity
    // thành JSON response
    // =========================

    private Map<String, Object> toMessageResponse(
            ConversationMessage message
    ) {

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("id", message.getId());
        response.put("role", message.getRole().name());
        response.put("content", message.getContent());
        response.put("createdAt", message.getCreatedAt());

        return response;
    }
}