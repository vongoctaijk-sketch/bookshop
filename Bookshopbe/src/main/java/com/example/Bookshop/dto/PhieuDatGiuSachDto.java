package com.example.Bookshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhieuDatGiuSachDto {

    private Integer id;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private String trangThai;
    private Integer khachHangId;
    private List<Integer> chiTietPhieuGiuIds;
}
