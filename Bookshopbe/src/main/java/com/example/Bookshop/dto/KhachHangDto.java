package com.example.Bookshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangDto {

    private Integer id;
    private String hoTen;
    private String sdt;
    private String email;
    private List<Integer> hoaDonIds;
    private List<Integer> phieuDatGiuSachIds;
}
