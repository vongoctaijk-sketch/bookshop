package com.example.Bookshop.controller;

import com.example.Bookshop.dto.AdminReportDto;
import com.example.Bookshop.entity.ChiTietHoaDon;
import com.example.Bookshop.entity.HoaDon;
import com.example.Bookshop.entity.Sach;
import com.example.Bookshop.repository.HoaDonRepository;
import com.example.Bookshop.repository.SachRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {
    private final HoaDonRepository hoaDonRepository;
    private final SachRepository sachRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public AdminReportDto getReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        LocalDate today = LocalDate.now();
        LocalDate start = from == null ? today.withDayOfMonth(1) : from;
        LocalDate end = to == null ? today : to;
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc");
        }
        LocalDateTime startAt = start.atStartOfDay();
        LocalDateTime endExclusive = end.plusDays(1).atStartOfDay();
        List<HoaDon> allCompleted = hoaDonRepository.findAll().stream()
                .filter(order -> "COMPLETED".equalsIgnoreCase(order.getTrangThai()))
                .toList();
        List<HoaDon> completed = allCompleted.stream()
                .filter(order -> order.getNgayBan() != null && !order.getNgayBan().isBefore(startAt) && order.getNgayBan().isBefore(endExclusive))
                .toList();
        BigDecimal revenue = completed.stream().map(HoaDon::getTongTien)
                .filter(value -> value != null).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal todayRevenue = allCompleted.stream()
                .filter(order -> order.getNgayBan().toLocalDate().equals(today))
                .map(HoaDon::getTongTien).filter(value -> value != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> byDay = new HashMap<>();
        Map<String, BigDecimal> byMonth = new HashMap<>();
        Map<String, BigDecimal> byYear = new HashMap<>();
        Map<String, BigDecimal> byCategory = new HashMap<>();
        Map<Integer, long[]> bookSales = new HashMap<>();
        Map<Integer, BigDecimal> bookRevenue = new HashMap<>();
        long unitsSold = 0;
        for (HoaDon order : completed) {
            BigDecimal amount = order.getTongTien() == null ? BigDecimal.ZERO : order.getTongTien();
            byDay.merge(order.getNgayBan().toLocalDate().toString(), amount, BigDecimal::add);
            byMonth.merge(YearMonth.from(order.getNgayBan()).toString(), amount, BigDecimal::add);
            byYear.merge(Integer.toString(order.getNgayBan().getYear()), amount, BigDecimal::add);
            for (ChiTietHoaDon item : order.getChiTietHoaDons() == null ? List.<ChiTietHoaDon>of() : order.getChiTietHoaDons()) {
                if (item.getSach() == null) continue;
                unitsSold += item.getSoLuong() == null ? 0 : item.getSoLuong();
                Integer bookId = item.getSach().getId();
                bookSales.computeIfAbsent(bookId, ignored -> new long[1])[0] += item.getSoLuong() == null ? 0 : item.getSoLuong();
                bookRevenue.merge(bookId, item.getThanhTien() == null ? BigDecimal.ZERO : item.getThanhTien(), BigDecimal::add);
                String category = item.getSach().getTheLoai() == null ? "Chưa phân loại" : item.getSach().getTheLoai().getTenTheLoai();
                byCategory.merge(category, item.getThanhTien() == null ? BigDecimal.ZERO : item.getThanhTien(), BigDecimal::add);
            }
        }
        List<Sach> books = sachRepository.findAll();
        Map<Integer, Sach> bookById = books.stream().collect(Collectors.toMap(Sach::getId, book -> book));
        List<AdminReportDto.BookMetric> bestSellers = bookSales.entrySet().stream()
                .sorted(Map.Entry.<Integer, long[]>comparingByValue(Comparator.comparingLong(value -> value[0])).reversed())
                .limit(10).filter(entry -> bookById.containsKey(entry.getKey()))
                .map(entry -> metric(bookById.get(entry.getKey()), (int) entry.getValue()[0], bookRevenue.get(entry.getKey())))
                .toList();
        List<AdminReportDto.BookMetric> lowestStock = books.stream()
                .sorted(Comparator.comparing(Sach::getSoLuongTon, Comparator.nullsFirst(Integer::compareTo)))
                .limit(10).map(book -> metric(book, book.getSoLuongTon(), bookRevenue.getOrDefault(book.getId(), BigDecimal.ZERO))).toList();
        List<AdminReportDto.BookMetric> highestStock = books.stream()
                .sorted(Comparator.comparing(Sach::getSoLuongTon, Comparator.nullsLast(Integer::compareTo)).reversed())
                .limit(10).map(book -> metric(book, book.getSoLuongTon(), bookRevenue.getOrDefault(book.getId(), BigDecimal.ZERO))).toList();

        return new AdminReportDto(revenue, todayRevenue, completed.size(), unitsSold,
                completed.isEmpty() ? BigDecimal.ZERO : revenue.divide(BigDecimal.valueOf(completed.size()), 2, java.math.RoundingMode.HALF_UP),
                points(byDay), points(byMonth), points(byYear),
                byCategory.entrySet().stream().map(entry -> new AdminReportDto.CategoryRevenue(entry.getKey(), entry.getValue()))
                        .sorted(Comparator.comparing(AdminReportDto.CategoryRevenue::getRevenue).reversed()).toList(),
                bestSellers, lowestStock, highestStock);
    }

    private AdminReportDto.BookMetric metric(Sach book, Integer quantity, BigDecimal revenue) {
        return new AdminReportDto.BookMetric(book.getId(), book.getTenSach(), quantity, revenue == null ? BigDecimal.ZERO : revenue);
    }

    private List<AdminReportDto.RevenuePoint> points(Map<String, BigDecimal> data) {
        return data.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .map(entry -> new AdminReportDto.RevenuePoint(entry.getKey(), entry.getValue())).toList();
    }
}
