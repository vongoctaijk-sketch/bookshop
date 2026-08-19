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
public class ChiTietPhieuNhapDto {

    private Integer id;
    private Integer soLuong;
    private BigDecimal giaNhap;
    private Integer phieuNhapId;
    private Integer sachId;
}
