package com.example.Bookshop.service;

import com.example.Bookshop.entity.Sach;
import com.example.Bookshop.repository.SachEmbeddingRepository;
import com.example.Bookshop.repository.SachRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookRagService {

    private static final String NO_CONTEXT = "Không có ngữ cảnh sách liên quan.";

    private final SachRepository sachRepository;
    private final SachEmbeddingRepository embeddingRepository;
    private final EmbeddingService embeddingService;
    private final BookEmbeddingService bookEmbeddingService;
    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    public String retrieve(String query, int limit) {
        if (query == null || query.isBlank() || !isPostgres()) {
            return NO_CONTEXT;
        }

        try {
            String queryVector = embeddingService.embedAsVector(query);
            List<Integer> bookIds = embeddingRepository.findNearestIds(queryVector, limit);
            if (bookIds.isEmpty() && embeddingRepository.isEmpty()) {
                for (Sach book : sachRepository.findAll()) {
                    bookEmbeddingService.index(book);
                }
                bookIds = embeddingRepository.findNearestIds(queryVector, limit);
            }
            if (bookIds.isEmpty()) {
                return NO_CONTEXT;
            }

            Map<Integer, Sach> books = sachRepository.findAllById(bookIds).stream()
                    .collect(Collectors.toMap(Sach::getId, Function.identity()));
            return bookIds.stream()
                    .map(books::get)
                    .filter(book -> book != null)
                    .map(this::toContext)
                    .collect(Collectors.joining("\n"));
        } catch (Exception exception) {
            return "Không thể truy xuất ngữ cảnh semantic lúc này.";
        }
    }

    private String toContext(Sach book) {
        String authors = book.getTacGias() == null ? "N/A" : book.getTacGias().stream()
                .map(author -> author.getHoTen())
                .collect(Collectors.joining(", "));
        return String.format("- %s | Mô tả: %s | Thể loại: %s | Tác giả: %s | Giá hiện tại: %s | Tồn kho hiện tại: %s",
                book.getTenSach(), book.getMoTa(),
                book.getTheLoai() == null ? "N/A" : book.getTheLoai().getTenTheLoai(),
                authors, book.getGiaBan(), book.getSoLuongTon());
    }

    private boolean isPostgres() {
        return datasourceUrl.startsWith("jdbc:postgresql:");
    }
}
