package com.example.Bookshop.service.ai;

import com.example.Bookshop.entity.Sach;
import com.example.Bookshop.service.NhaXuatBanService;
import com.example.Bookshop.service.SachService;
import com.example.Bookshop.service.TacGiaService;
import com.example.Bookshop.service.TheLoaiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookTools {

    private final SachService sachService;
    private final TheLoaiService theLoaiService;
    private final TacGiaService tacGiaService;
    private final NhaXuatBanService nhaXuatBanService;

    public record CategorySummary(Integer id, String tenTheLoai, String moTa) {}
    public record AuthorSummary(Integer id, String hoTen, String quocTich) {}
    public record PublisherSummary(Integer id, String tenNxb, String diaChi) {}

    public List<BookSearchResult> searchBooks(String keyword) {
        List<Sach> books = sachService.searchBooks(keyword);
        return books.stream().map(this::toBookResult).collect(Collectors.toList());
    }

    public List<BookSearchResult> getTopSellingBooks() {
        return sachService.topsachBanChayNhat().stream()
                .map(this::toBookResult)
                .collect(Collectors.toList());
    }

    public List<BookSearchResult> getBooksByCategory(Integer categoryId) {
        if (categoryId == null) {
            return List.of();
        }

        return sachService.getAll().stream()
                .filter(s -> s.getTheLoai() != null && categoryId.equals(s.getTheLoai().getId()))
                .map(this::toBookResult)
                .collect(Collectors.toList());
    }

    public List<BookSearchResult> getBooksByAuthor(Integer authorId) {
        if (authorId == null) {
            return List.of();
        }

        return sachService.getAll().stream()
                .filter(s -> s.getTacGias() != null && s.getTacGias().stream().anyMatch(t -> authorId.equals(t.getId())))
                .map(this::toBookResult)
                .collect(Collectors.toList());
    }

    public List<BookSearchResult> getBooksByPublisher(Integer publisherId) {
        if (publisherId == null) {
            return List.of();
        }

        return sachService.getAll().stream()
                .filter(s -> s.getNhaXuatBan() != null && publisherId.equals(s.getNhaXuatBan().getId()))
                .map(this::toBookResult)
                .collect(Collectors.toList());
    }

    public List<BookSearchResult> getBookPrice(String titleKeyword) {
        if (titleKeyword == null || titleKeyword.isBlank()) {
            return List.of();
        }

        return sachService.getAll().stream()
                .filter(s -> s.getTenSach() != null && s.getTenSach().toLowerCase().contains(titleKeyword.toLowerCase().trim()))
                .map(this::toBookResult)
                .collect(Collectors.toList());
    }

    public List<BookSearchResult> getBooksByCategoryName(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            return List.of();
        }

        return sachService.getAll().stream()
                .filter(s -> s.getTheLoai() != null && s.getTheLoai().getTenTheLoai() != null
                        && s.getTheLoai().getTenTheLoai().toLowerCase().contains(categoryName.toLowerCase().trim()))
                .map(this::toBookResult)
                .collect(Collectors.toList());
    }

    public List<BookSearchResult> getBooksByAuthorName(String authorName) {
        if (authorName == null || authorName.isBlank()) {
            return List.of();
        }

        String target = authorName.toLowerCase().trim();
        return sachService.getAll().stream()
                .filter(s -> s.getTacGias() != null && s.getTacGias().stream().anyMatch(
                        t -> t.getHoTen() != null && t.getHoTen().toLowerCase().contains(target)))
                .map(this::toBookResult)
                .collect(Collectors.toList());
    }

    public List<BookSearchResult> getBooksByPublisherName(String publisherName) {
        if (publisherName == null || publisherName.isBlank()) {
            return List.of();
        }

        return sachService.getAll().stream()
                .filter(s -> s.getNhaXuatBan() != null && s.getNhaXuatBan().getTenNxb() != null
                        && s.getNhaXuatBan().getTenNxb().toLowerCase().contains(publisherName.toLowerCase().trim()))
                .map(this::toBookResult)
                .collect(Collectors.toList());
    }

    public List<BookSearchResult> getBooksByPriceRange(Number minPrice, Number maxPrice) {
        BigDecimal min = minPrice == null ? BigDecimal.ZERO : new BigDecimal(minPrice.toString());
        BigDecimal max = maxPrice == null ? null : new BigDecimal(maxPrice.toString());

        return sachService.getAll().stream()
                .filter(s -> s.getGiaBan() != null)
                .filter(s -> s.getGiaBan().compareTo(min) >= 0)
                .filter(s -> max == null || s.getGiaBan().compareTo(max) <= 0)
                .map(this::toBookResult)
                .collect(Collectors.toList());
    }

    public List<CategorySummary> getCategories() {
        return theLoaiService.getAll().stream()
                .map(t -> new CategorySummary(t.getId(), t.getTenTheLoai(), t.getMoTa()))
                .collect(Collectors.toList());
    }

    public List<AuthorSummary> getAuthors() {
        return tacGiaService.getAll().stream()
                .map(t -> new AuthorSummary(t.getId(), t.getHoTen(), t.getQuocTich()))
                .collect(Collectors.toList());
    }

    public List<PublisherSummary> getPublishers() {
        return nhaXuatBanService.getAll().stream()
                .map(n -> new PublisherSummary(n.getId(), n.getTenNxb(), n.getDiaChi()))
                .collect(Collectors.toList());
    }

    private BookSearchResult toBookResult(Sach book) {
        return new BookSearchResult(
                book.getId(),
                book.getTenSach(),
                book.getGiaBan(),
                book.getSoLuongTon()
        );
    }
}
