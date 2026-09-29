package com.example.Bookshop.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {

    private final String apiKey;
    private final String model;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public EmbeddingService(
            @Value("${gemini.api-key:${GOOGLE_API_KEY:}}") String apiKey,
            @Value("${gemini.embedding-model:gemini-embedding-001}") String model) {
        this.apiKey = apiKey;
        this.model = model;
    }

    public String embedAsVector(String text) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Gemini API key chưa được cấu hình");
        }
        try {
            String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/"
                    + model + ":embedContent?key=" + apiKey.trim();
            String body = objectMapper.writeValueAsString(Map.of(
                    "model", "models/" + model,
                    "content", Map.of("parts", List.of(Map.of("text", text)))
            ));
            HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                throw new IllegalStateException("Embedding API trả về HTTP " + response.statusCode());
            }

            JsonNode values = objectMapper.readTree(response.body()).path("embedding").path("values");
            if (!values.isArray() || values.isEmpty()) {
                throw new IllegalStateException("Embedding API không trả về vector");
            }
            StringBuilder vector = new StringBuilder("[");
            for (int index = 0; index < values.size(); index++) {
                if (index > 0) {
                    vector.append(',');
                }
                vector.append(values.get(index).asDouble());
            }
            return vector.append(']').toString();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Embedding request bị gián đoạn", exception);
        } catch (Exception exception) {
            throw new IllegalStateException("Không thể tạo embedding: " + exception.getMessage(), exception);
        }
    }
}
