package com.example.Bookshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class AdminReportDto {
    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;
    private long completedOrders;
    private long unitsSold;
    private BigDecimal averageOrderValue;
    private List<RevenuePoint> revenueByDay;
    private List<RevenuePoint> revenueByMonth;
    private List<RevenuePoint> revenueByYear;
    private List<CategoryRevenue> revenueByCategory;
    private List<BookMetric> bestSellers;
    private List<BookMetric> lowestStock;
    private List<BookMetric> highestStock;

    @Getter
    @AllArgsConstructor
    public static class RevenuePoint {
        private String period;
        private BigDecimal revenue;
    }

    @Getter
    @AllArgsConstructor
    public static class CategoryRevenue {
        private String category;
        private BigDecimal revenue;
    }

    @Getter
    @AllArgsConstructor
    public static class BookMetric {
        private Integer bookId;
        private String title;
        private Integer quantity;
        private BigDecimal revenue;
    }
}
