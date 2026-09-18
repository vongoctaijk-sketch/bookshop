package com.example.Bookshop.service;

import com.example.Bookshop.entity.Sach;
import com.example.Bookshop.repository.SachRepository;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeminiService {

    private final String apiKey;
    private final String model;
    private final Client client;
    private final SachRepository sachRepository;
    public GeminiService(
            @Value("${gemini.api-key:${GOOGLE_API_KEY:}}") String apiKey,
            @Value("${gemini.model:gemini-2.5-flash}") String model,
            SachRepository sachRepository) {
        this.apiKey = apiKey;
        this.model = model;
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
        this.sachRepository=sachRepository;
    }

    public String generateText(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt không được rỗng");
        }

        String key = apiKey == null ? "" : apiKey.trim();
        if (key.isEmpty()) {
            throw new IllegalStateException("Gemini API key chưa được cấu hình");
        }
        List<Sach> books = sachRepository.findAll();

        String context = buildBookContext(books);
        try {
            String fullPrompt = """
                Bạn là trợ lý AI của cửa hàng sách Bookshop.

                Hãy trả lời khách hàng dựa trên dữ liệu sách được cung cấp bên dưới.

                Quy tắc:
                - Chỉ sử dụng thông tin có trong dữ liệu.
                - Không tự bịa tên sách, giá, tác giả hoặc tồn kho.
                - Nếu dữ liệu không đủ để trả lời, hãy nói rõ rằng bạn chưa có thông tin.
                - Trả lời bằng tiếng Việt, thân thiện và ngắn gọn.

                DỮ LIỆU SÁCH:
                %s

                CÂU HỎI KHÁCH HÀNG:
                %s
                """.formatted(context, prompt);

            GenerateContentResponse response =
                    client.models.generateContent(
                            model,
                            fullPrompt,
                            null
                    );

            return response.text();
        } catch (Exception ex) {
            throw new IllegalStateException("Không thể gọi Gemini API: " + ex.getMessage(), ex);
        }
    }
    private String buildBookContext(List<Sach> books) {

        if (books == null || books.isEmpty()) {
            return "Hiện tại không có dữ liệu sách.";
        }

        StringBuilder context = new StringBuilder();

        for (Sach book : books) {

            context.append("\n--- SÁCH ---\n");

            context.append("Tên sách: ")
                    .append(book.getTenSach())
                    .append("\n");

            context.append("Mô tả: ")
                    .append(book.getMoTa())
                    .append("\n");

            context.append("Giá bán: ")
                    .append(book.getGiaBan())
                    .append(" VNĐ\n");

            context.append("Số lượng tồn: ")
                    .append(book.getSoLuongTon())
                    .append("\n");

            context.append("Năm xuất bản: ")
                    .append(book.getNamXuatBan())
                    .append("\n");

            context.append("Đang kinh doanh: ")
                    .append(book.getDangKinhDoanh())
                    .append("\n");

            if (book.getTheLoai() != null) {
                context.append("Thể loại: ")
                        .append(book.getTheLoai().getTenTheLoai())
                        .append("\n");
            }

            if (book.getNhaXuatBan() != null) {
                context.append("Nhà xuất bản: ")
                        .append(book.getNhaXuatBan().getTenNxb())
                        .append("\n");
            }

            if (book.getTacGias() != null && !book.getTacGias().isEmpty()) {

                context.append("Tác giả: ");

                context.append(
                        book.getTacGias()
                                .stream()
                                .map(tacGia -> tacGia.getHoTen())
                                .reduce((a, b) -> a + ", " + b)
                                .orElse("")
                );

                context.append("\n");
            }
        }

        return context.toString();}
}
