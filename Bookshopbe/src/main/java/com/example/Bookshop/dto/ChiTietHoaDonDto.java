package com.example.Bookshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietHoaDonDto {

    private Integer id;
    private Integer soLuong;
    private BigDecimal donGia;
    private String hinhAnh;
    private String tenSach;
    private BigDecimal thanhTien;
    private Integer hoaDonId;
    private Integer sachId;
}
