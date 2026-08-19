package com.example.Bookshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SachDto {

    private Integer id;
    private String tenSach;
    private BigDecimal giaBan;
    private Integer soLuongTon;
    private String hinhAnh;
    private Integer namXuatBan;
    private Boolean dangKinhDoanh;
    private Integer theLoaiId;
    private Integer nhaXuatBanId;
    private List<Integer> tacGiaIds;
}
