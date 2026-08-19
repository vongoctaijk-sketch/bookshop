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
public class TheLoaiDto {

    private Integer id;
    private String tenTheLoai;
    private String moTa;
    private List<Integer> sachIds;
}
