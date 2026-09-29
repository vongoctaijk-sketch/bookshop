package com.example.Bookshop.service;

import com.example.Bookshop.entity.ConversationMessage;
import com.example.Bookshop.entity.MessageRole;
import com.example.Bookshop.service.ai.BookTools;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeminiService {

    private final String apiKey;
    private final String model;
    private final Client client;
    private final ConversationService conversationService;
    private final BookTools bookTools;
    private final BookRagService bookRagService;

    public GeminiService(
            @Value("${gemini.api-key:${GOOGLE_API_KEY:}}") String apiKey,
            @Value("${gemini.model:gemini-2.5-flash}") String model,
            ConversationService conversationService,
            BookTools bookTools,
            BookRagService bookRagService) {
        this.apiKey = apiKey;
        this.model = model;
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
        this.conversationService = conversationService;
        this.bookTools = bookTools;
        this.bookRagService = bookRagService;
    }

    public String generateText(Integer conversationId, String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt không được rỗng");
        }

        String key = apiKey == null ? "" : apiKey.trim();
        if (key.isEmpty()) {
            throw new IllegalStateException("Gemini API key chưa được cấu hình");
        }

        List<ConversationMessage> history = conversationService.getMessages(conversationId);
        String conversationContext = buildConversationContext(history);
        String retrievedContext = bookRagService.retrieve(prompt, 5);

        String systemInstruction = """
                Bạn là trợ lý AI của cửa hàng sách Bookshop.

                QUY TẮC (quan trọng):
                - Không tự bịa dữ liệu sách (tên, giá, tồn kho).
                - Ưu tiên ngữ cảnh sách được truy xuất bằng embedding bên dưới; giá và tồn kho trong ngữ cảnh là dữ liệu hiện tại.
                - Khi cần thông tin thực tế, hãy gọi tool phù hợp.
                - Chỉ dùng dữ liệu từ tool để trả lời.
                - Các tool hỗ trợ:
                  1. searchBooks: {"function":"searchBooks","arguments":{"keyword":"..."}}
                  2. getBookPrice: {"function":"getBookPrice","arguments":{"titleKeyword":"..."}}
                  3. getTopSellingBooks: {"function":"getTopSellingBooks","arguments":{}}
                  4. getBooksByCategory: {"function":"getBooksByCategory","arguments":{"categoryId":1}}
                  5. getBooksByCategoryName: {"function":"getBooksByCategoryName","arguments":{"categoryName":"..."}}
                  6. getBooksByAuthor: {"function":"getBooksByAuthor","arguments":{"authorId":1}}
                  7. getBooksByAuthorName: {"function":"getBooksByAuthorName","arguments":{"authorName":"..."}}
                  8. getBooksByPublisher: {"function":"getBooksByPublisher","arguments":{"publisherId":1}}
                  9. getBooksByPublisherName: {"function":"getBooksByPublisherName","arguments":{"publisherName":"..."}}
                  10. getBooksByPriceRange: {"function":"getBooksByPriceRange","arguments":{"minPrice":50000,"maxPrice":200000}}
                  11. getCategories: {"function":"getCategories","arguments":{}}
                  12. getAuthors: {"function":"getAuthors","arguments":{}}
                  13. getPublishers: {"function":"getPublishers","arguments":{}}
                - Nếu gọi tool, hãy trả về đúng JSON theo mẫu, không in thêm chữ khác.
                - Nếu không cần tool, trả lời trực tiếp bằng tiếng Việt.
                - Nếu tool trả về danh sách rỗng, hãy nói rõ không tìm thấy.
                - Không tiết lộ chi tiết kỹ thuật Function Calling cho khách hàng.
                """;

        try {
            String firstPrompt = String.format("%s\n\nNgữ cảnh RAG liên quan:\n%s\n\nLịch sử:\n%s\n\nCâu hỏi mới:\n%s", systemInstruction, retrievedContext, conversationContext, prompt);
            GenerateContentResponse firstResp = client.models.generateContent(model, firstPrompt, null);
            String firstText = firstResp.text();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = null;
            try {
                root = mapper.readTree(firstText.trim());
            } catch (Exception e) {
                int idx = firstText.indexOf('{');
                if (idx >= 0) {
                    String possible = firstText.substring(idx);
                    try {
                        root = mapper.readTree(possible);
                    } catch (Exception ex) {
                        root = null;
                    }
                }
            }

            if (root != null && root.has("function")) {
                String functionName = root.path("function").asText();
                Object toolResult;

                try {
                    switch (functionName) {
                        case "searchBooks" -> {
                            JsonNode arguments = root.path("arguments");
                            String keyword = arguments.path("keyword").asText();
                            toolResult = bookTools.searchBooks(keyword);
                        }
                        case "getBookPrice" -> {
                            JsonNode arguments = root.path("arguments");
                            String titleKeyword = arguments.path("titleKeyword").asText();
                            toolResult = bookTools.getBookPrice(titleKeyword);
                        }
                        case "getTopSellingBooks" -> toolResult = bookTools.getTopSellingBooks();
                        case "getBooksByCategory" -> {
                            JsonNode arguments = root.path("arguments");
                            Integer categoryId = arguments.has("categoryId") && !arguments.path("categoryId").isNull()
                                    ? arguments.path("categoryId").asInt()
                                    : null;
                            toolResult = bookTools.getBooksByCategory(categoryId);
                        }
                        case "getBooksByCategoryName" -> {
                            JsonNode arguments = root.path("arguments");
                            String categoryName = arguments.path("categoryName").asText();
                            toolResult = bookTools.getBooksByCategoryName(categoryName);
                        }
                        case "getBooksByAuthor" -> {
                            JsonNode arguments = root.path("arguments");
                            Integer authorId = arguments.has("authorId") && !arguments.path("authorId").isNull()
                                    ? arguments.path("authorId").asInt()
                                    : null;
                            toolResult = bookTools.getBooksByAuthor(authorId);
                        }
                        case "getBooksByAuthorName" -> {
                            JsonNode arguments = root.path("arguments");
                            String authorName = arguments.path("authorName").asText();
                            toolResult = bookTools.getBooksByAuthorName(authorName);
                        }
                        case "getBooksByPublisher" -> {
                            JsonNode arguments = root.path("arguments");
                            Integer publisherId = arguments.has("publisherId") && !arguments.path("publisherId").isNull()
                                    ? arguments.path("publisherId").asInt()
                                    : null;
                            toolResult = bookTools.getBooksByPublisher(publisherId);
                        }
                        case "getBooksByPublisherName" -> {
                            JsonNode arguments = root.path("arguments");
                            String publisherName = arguments.path("publisherName").asText();
                            toolResult = bookTools.getBooksByPublisherName(publisherName);
                        }
                        case "getBooksByPriceRange" -> {
                            JsonNode arguments = root.path("arguments");
                            Number minPrice = arguments.has("minPrice") && !arguments.path("minPrice").isNull()
                                    ? arguments.path("minPrice").numberValue()
                                    : null;
                            Number maxPrice = arguments.has("maxPrice") && !arguments.path("maxPrice").isNull()
                                    ? arguments.path("maxPrice").numberValue()
                                    : null;
                            toolResult = bookTools.getBooksByPriceRange(minPrice, maxPrice);
                        }
                        case "getCategories" -> toolResult = bookTools.getCategories();
                        case "getAuthors" -> toolResult = bookTools.getAuthors();
                        case "getPublishers" -> toolResult = bookTools.getPublishers();
                        default -> throw new IllegalArgumentException("Tool '" + functionName + "' không được hỗ trợ.");
                    }
                } catch (Exception ex) {
                    return "Xin lỗi, hệ thống tìm kiếm gặp lỗi: " + ex.getMessage();
                }

                String funcResponseJson = mapper.writeValueAsString(toolResult);

                String secondPrompt = String.format(
                        "%s\n\nNgữ cảnh RAG liên quan:\n%s\n\nLịch sử:\n%s\n\nCâu hỏi mới:\n%s\n\nKết quả tool trả về:\n%s\n\nHãy tạo câu trả lời cuối cùng bằng tiếng Việt, ngắn gọn, chỉ dùng dữ liệu thực tế từ ngữ cảnh RAG và kết quả tool.",
                        systemInstruction, retrievedContext,
                        conversationContext,
                        prompt,
                        funcResponseJson
                );

                GenerateContentResponse finalResp = client.models.generateContent(model, secondPrompt, null);
                return finalResp.text();
            }

            return firstText;
        } catch (Exception ex) {
            throw new IllegalStateException("Không thể gọi Gemini API: " + ex.getMessage(), ex);
        }
    }

    private String buildConversationContext(List<ConversationMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return "Chưa có lịch sử trò chuyện.";
        }

        StringBuilder context = new StringBuilder();
        for (ConversationMessage message : messages) {
            String role = message.getRole() == MessageRole.USER ? "KHÁCH HÀNG" : "TRỢ LÝ";
            context.append(role).append(": ").append(message.getContent()).append("\n");
        }
        return context.toString();
    }
}

