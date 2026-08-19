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
public class NhaXuatBanDto {

    private Integer id;
    private String tenNxb;
    private String diaChi;
    private List<Integer> sachIds;
}
