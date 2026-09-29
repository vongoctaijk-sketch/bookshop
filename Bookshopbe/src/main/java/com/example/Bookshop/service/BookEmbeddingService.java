package com.example.Bookshop.service;

import com.example.Bookshop.entity.Sach;
import com.example.Bookshop.repository.SachEmbeddingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookEmbeddingService {

    private final SachEmbeddingRepository embeddingRepository;
    private final EmbeddingService embeddingService;
    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    public void index(Sach book) {
        if (book == null || book.getId() == null || !isPostgres()) {
            return;
        }
        String content = toContent(book);
        embeddingRepository.upsert(book.getId(), content, embeddingService.embedAsVector(content));
    }

    public String toContent(Sach book) {
        String authors = book.getTacGias() == null ? "" : book.getTacGias().stream()
                .map(author -> author.getHoTen())
                .collect(Collectors.joining(", "));
        String category = book.getTheLoai() == null ? "" : book.getTheLoai().getTenTheLoai();
        String publisher = book.getNhaXuatBan() == null ? "" : book.getNhaXuatBan().getTenNxb();
        return String.format("Tên sách: %s. Mô tả: %s. Thể loại: %s. Tác giả: %s. Nhà xuất bản: %s.",
                book.getTenSach(), book.getMoTa(), category, authors, publisher);
    }

    private boolean isPostgres() {
        return datasourceUrl.startsWith("jdbc:postgresql:");
    }
}
