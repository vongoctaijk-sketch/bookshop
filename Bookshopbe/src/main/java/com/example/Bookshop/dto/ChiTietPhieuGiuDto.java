package com.example.Bookshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietPhieuGiuDto {

    private Integer id;
    private Integer soLuong;
    private Integer holdOrderId;
    private Integer phieuGiuId;
    private Integer sachId;
}
