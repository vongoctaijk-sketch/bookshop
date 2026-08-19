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
public class PhieuNhapDto {

    private Integer id;
    private LocalDateTime ngayNhap;
    private BigDecimal tongTien;
    private Integer nhaCungCapId;
    private Integer nguoiDungId;
    private List<Integer> chiTietPhieuNhapIds;
    private List<Integer> danhSachChiTietIds;
}
