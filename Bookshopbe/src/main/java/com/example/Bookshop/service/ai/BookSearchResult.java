package com.example.Bookshop.service.ai;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BookSearchResult {
    private Integer id;
    private String tenSach;
    private BigDecimal giaBan;
    private Integer soLuongTon;
}
