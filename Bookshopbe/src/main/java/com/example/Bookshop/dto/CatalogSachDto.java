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
public class CatalogSachDto {

    private Integer id;
    private String tenSach;
    private String moTa;
    private BigDecimal giaBan;
    private Integer soLuongTon;
    private String hinhAnh;
    private Integer namXuatBan;
    private Boolean dangKinhDoanh;
    private TheLoaiDto theLoai;
    private NhaXuatBanDto nhaXuatBan;
    private List<TacGiaDto> tacGias;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TheLoaiDto {
        private Integer id;
        private String tenTheLoai;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NhaXuatBanDto {
        private Integer id;
        private String tenNxb;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TacGiaDto {
        private Integer id;
        private String hoTen;
    }
}