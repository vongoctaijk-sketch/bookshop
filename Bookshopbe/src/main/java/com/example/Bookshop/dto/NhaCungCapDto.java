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
public class NhaCungCapDto {

    private Integer id;
    private String tenNcc;
    private String sdt;
    private String diaChi;
    private List<Integer> phieuNhapIds;
}
