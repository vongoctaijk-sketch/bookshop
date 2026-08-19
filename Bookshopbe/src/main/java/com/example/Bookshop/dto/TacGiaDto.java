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
public class TacGiaDto {

    private Integer id;
    private String hoTen;
    private String quocTich;
    private List<Integer> sachIds;
}
