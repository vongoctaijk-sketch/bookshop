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
public class NguoiDungDto {

    private Integer id;
    private String hoTen;
    private String username;
    private String sdt;
    private String email;
    private String diaChi;
    private String nhomNguoiDungId;
    private List<Integer> hoaDonIds;
    private List<Integer> phieuNhapIds;
}
