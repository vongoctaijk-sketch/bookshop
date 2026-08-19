package com.example.Bookshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonDto {

    private Integer id;
    private String maHoaDon;
    private LocalDateTime ngayBan;
    private BigDecimal tongTien;
    private String trangThai;
    private Integer khachHangId;
    private Integer nguoiDungId;
    private List<Integer> chiTietHoaDonIds;
    private List<Integer> danhSachChiTietIds;
}
