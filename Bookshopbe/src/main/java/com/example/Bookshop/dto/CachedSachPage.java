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
public class CachedSachPage {

    private List<CatalogSachDto> content;
    private int page;
    private int size;
    private long totalElements;
}